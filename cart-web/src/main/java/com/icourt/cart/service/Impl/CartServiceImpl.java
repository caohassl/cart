package com.icourt.cart.service.Impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.icourt.cart.Enum.BussinessCartType;
import com.icourt.cart.Enum.DownloadType;
import com.icourt.cart.common.CartException;
import com.icourt.cart.config.CartConstant;
import com.icourt.cart.config.CartDownloadConstant;
import com.icourt.cart.dao.CartDao;
import com.icourt.cart.dto.CartBussinessDTO;
import com.icourt.cart.dto.JudgementInfoDTO;
import com.icourt.cart.dto.JudicialInfoDTO;
import com.icourt.cart.dto.LawReguInfoDTO;
import com.icourt.cart.entity.judgement.Judgement;
import com.icourt.cart.entity.judicialView.JudicialView;
import com.icourt.cart.entity.lawRegu.LawRegu;
import com.icourt.cart.entity.lawRegu.LexiscnParegraphVo;
import com.icourt.cart.exception.CartServiceException;
import com.icourt.cart.service.CartService;
import com.icourt.cart.utils.ExcelUtil;
import com.icourt.cart.vo.*;
import com.icourt.user.core.TokenUser;
import com.icourt.user.core.UserContext;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.groovy.util.ListHashMap;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Caomr on 2018/5/4.
 */
@Service
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;



    @Autowired
    private ServletContext servletContext;

    @Override
    public int getUserCartNumber() {
        TokenUser tokenUser = UserContext.getUser();

        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");

        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();
        int userCartNumber = 0;
        try {
            userCartNumber = cartDao.getUserCartNumber(userId,officeId,"0");
        } catch (Exception e) {
            log.error("入库失败", e);
            throw new CartServiceException("入库失败", e);
        }
        return userCartNumber;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBussinessCart(List<CartBussinessVO> cartBussinessVOs) {
        TokenUser tokenUser = UserContext.getUser();

        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");

        // 用户校验
        checkUser(tokenUser);
        // 数据校验
        checkCartBussinessVOs(cartBussinessVOs);
        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();

        List<String> judgementList = new ArrayList<>();
        List<Pair> lawreguList = new ArrayList<>();
        List<String> judicialList = new ArrayList<>();
        for (CartBussinessVO CartBussinessVO:cartBussinessVOs) {
            if(BussinessCartType.JUDGEMENT.name().equals(CartBussinessVO.getBussinessType())){
                judgementList.add(CartBussinessVO.getBussinessId());
            } else if(BussinessCartType.LAWREGU.name().equals(CartBussinessVO.getBussinessType())){
                Pair<String, String> pair = new Pair(CartBussinessVO.getBussinessId(),CartBussinessVO.getLawReguItem());
                lawreguList.add(pair);
            } else if(BussinessCartType.JUDICIAL.name().equals(CartBussinessVO.getBussinessType())){
                judicialList.add(CartBussinessVO.getBussinessId());
            }
        }
        Date now = new Date();
        // 分别查询ES数据
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        if(judgementList.size() > 0){
            cartBussinessDTOs.addAll(searchJudgement(judgementList, userId, officeId, now));
        }
        if(lawreguList.size() > 0){
            cartBussinessDTOs.addAll(searchLawregu(lawreguList, userId, officeId, now));
        }
        if(judicialList.size() > 0){
            cartBussinessDTOs.addAll(searchJudicial(judicialList, userId, officeId, now));
        }
        // 校验数据是否完全匹配
        checkData(cartBussinessVOs, cartBussinessDTOs);
        // 数据入库
        try{
            cartDao.insertCarts(cartBussinessDTOs);
        }catch (Exception e){
            log.error("入库失败", e);
            throw new CartServiceException("入库失败", e);
        }

    }

    @Override
    public CartBussinessListVO selectCartsByUser() {
        TokenUser tokenUser = UserContext.getUser();

        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");

        // 用户校验
        checkUser(tokenUser);
        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();
        List<CartBussinessDTO> cartBussinessDTOs = null;
        try {
            cartBussinessDTOs = cartDao.selectCartsByUser(userId, officeId, "0" ,null);
        } catch (Exception e) {
            log.error("数据库查询失败", e);
            throw new CartServiceException("数据库查询失败", e);
        }
        List<JudgementVO> judgementList = new ArrayList<>();
        List<LawReguVO> lawReguList = new ArrayList<>();
        List<JudicialVO> judicialList = new ArrayList<>();
        for (CartBussinessDTO cartBussinessDTO:cartBussinessDTOs) {
            if(BussinessCartType.JUDGEMENT.name().equals(cartBussinessDTO.getBussinessType())){
                addJudgementList(judgementList, cartBussinessDTO);
            }else if(BussinessCartType.LAWREGU.name().equals(cartBussinessDTO.getBussinessType())){
                addLawReguList(lawReguList, cartBussinessDTO);
            }else if(BussinessCartType.JUDICIAL.name().equals(cartBussinessDTO.getBussinessType())){
                addJudicialList(judicialList, cartBussinessDTO);
            }
        }
        CartBussinessListVO cartBussinessListVO = new CartBussinessListVO();
        cartBussinessListVO.setJudgementList(judgementList);
        cartBussinessListVO.setLawReguList(lawReguList);
        cartBussinessListVO.setJudicialList(judicialList);
        cartBussinessListVO.setJudgementListCount(judgementList.size());
        cartBussinessListVO.setLawReguListCount(lawReguList.size());
        cartBussinessListVO.setJudgementListCount(judicialList.size());
        cartBussinessListVO.setCount(cartBussinessDTOs.size());
        return cartBussinessListVO;
    }

    private void addJudicialList(List<JudicialVO> judicialList, CartBussinessDTO cartBussinessDTO) {
        JudicialVO judicialVO = new JudicialVO();
        judicialVO.setCartId(cartBussinessDTO.getCartId());
        judicialVO.setVid(cartBussinessDTO.getBussinessId());
        judicialVO.setTitle(cartBussinessDTO.getBussinessTitle());
        judicialVO.setFullContent(cartBussinessDTO.getBussinessContent());
        JudicialInfoDTO judicialInfoDTO;
        try {
            judicialInfoDTO = JSON.parseObject(cartBussinessDTO.getBussinessBasicInfo(),JudicialInfoDTO.class);
        } catch (Exception e) {
            log.error("格式化json错误",e);
            throw new CartServiceException("格式化json错误",e);
        }
        judicialVO.setKeywordsArr(judicialInfoDTO.getKeywordsArr());
        judicialVO.setContent(judicialInfoDTO.getContent());
        judicialList.add(judicialVO);
    }

    private void addLawReguList(List<LawReguVO> lawReguList, CartBussinessDTO cartBussinessDTO) {
        LawReguVO lawReguVO = new LawReguVO();
        lawReguVO.setCartId(cartBussinessDTO.getCartId());
        lawReguVO.setLid(cartBussinessDTO.getBussinessId());
        lawReguVO.setTitle(cartBussinessDTO.getBussinessTitle());
        lawReguVO.setFullContent(cartBussinessDTO.getBussinessContent());
        LawReguInfoDTO lawReguInfoDTO;
        try {
            lawReguInfoDTO = JSON.parseObject(cartBussinessDTO.getBussinessBasicInfo(),LawReguInfoDTO.class);
        } catch (Exception e) {
            log.error("格式化json错误",e);
            throw new CartServiceException("格式化json错误",e);
        }
        lawReguVO.setEffLevel(lawReguInfoDTO.getEffLevel());
        lawReguVO.setTimeLimited(lawReguInfoDTO.getTimeLimited());
        lawReguVO.setDocumentNumber(lawReguInfoDTO.getDocumentNumber());
        lawReguVO.setPostingDate(lawReguInfoDTO.getPostingDate());
        lawReguVO.setEffectiveDate(lawReguInfoDTO.getEffectiveDate());
        lawReguVO.setDispatchAuthority(lawReguInfoDTO.getDispatchAuthority());
        lawReguVO.setContent(lawReguInfoDTO.getContent());
        lawReguVO.setFullName(lawReguInfoDTO.getFullName());
        lawReguList.add(lawReguVO);
    }

    private void addJudgementList(List<JudgementVO> judgementList, CartBussinessDTO cartBussinessDTO) {
        JudgementVO judgementVO = new JudgementVO();
        judgementVO.setCartId(cartBussinessDTO.getCartId());
        judgementVO.setJid(cartBussinessDTO.getBussinessId());
        judgementVO.setTitle(cartBussinessDTO.getBussinessTitle());
        judgementVO.setFullContent(cartBussinessDTO.getBussinessContent());
        JudgementInfoDTO judgementInfoDTO;
        try {
            judgementInfoDTO = JSON.parseObject(cartBussinessDTO.getBussinessBasicInfo(),JudgementInfoDTO.class);
        } catch (Exception e) {
            log.error("格式化json错误",e);
            throw new CartServiceException("格式化json错误",e);
        }
        judgementVO.setCaseType(judgementInfoDTO.getCaseType());
        judgementVO.setJudgementType(judgementInfoDTO.getJudgementType());
        judgementVO.setCaseNumber(judgementInfoDTO.getCaseNumber());
        judgementVO.setJudgementDate(judgementInfoDTO.getJudgementDate());
        judgementVO.setPublicType(judgementInfoDTO.getPublicType());
        judgementVO.setLeveloftria(judgementInfoDTO.getLeveloftria());
        judgementVO.setContent(judgementInfoDTO.getContent());
        judgementList.add(judgementVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCartScore(List<CartSortVO> cartSortVOs) {
        TokenUser tokenUser = UserContext.getUser();

        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");

        // 用户校验
        checkUser(tokenUser);
        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();

        for (CartSortVO cartSortVO:cartSortVOs) {
            try{
                cartDao.updateCartScore(cartSortVO.getCartId(), userId, officeId, cartSortVO.getSort());
            }catch (Exception e){
                log.error("入库失败", e);
                throw new CartServiceException("入库失败", e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Integer> cartBussinessIds) {
        TokenUser tokenUser = UserContext.getUser();

        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");

        // 用户校验
        checkUser(tokenUser);
        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();

        for (Integer cartId:cartBussinessIds) {
            cartDao.updateCartBussinessStatus(cartId, userId, officeId, "2");
        }
    }

    @Override
    public void downloadBussinessCart(Integer type, String bussinessType) {
        TokenUser tokenUser = UserContext.getUser();

        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");
        tokenUser.setUserName("test");

        // 用户校验
        checkUser(tokenUser);

        // 校验下载权限
        // todo

        // 数据校验
        checkType(type);
        // 数据校验
        checkBussinessType(bussinessType);
        // 如果为空转化为report
        if(StringUtils.isBlank(bussinessType)){
            bussinessType = BussinessCartType.REPORT.name();
        }
        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();

        // 从数据库查询user对应的收藏id
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        try {
            cartBussinessDTOs = StringUtils.isBlank(bussinessType) ?
                    cartDao.selectCartsByUser(userId, officeId, "0", null) :
                    cartDao.selectCartsByUser(userId, officeId, "0", bussinessType) ;
        } catch (Exception e) {
            log.error("数据库查询失败", e);
            throw new CartServiceException("数据库查询失败", e);
        }
        List<String> judgementList = new ArrayList<>();
        List<Pair<String, String>> lawReguList = new ArrayList<>();
        List<String> judicialList = new ArrayList<>();
        for (CartBussinessDTO cartBussinessDTO:cartBussinessDTOs) {
            if(BussinessCartType.JUDGEMENT.name().equals(cartBussinessDTO.getBussinessType())){
                judgementList.add(cartBussinessDTO.getBussinessId());
            } else if(BussinessCartType.LAWREGU.name().equals(cartBussinessDTO.getBussinessType())){
                LawReguInfoDTO lawReguInfoDTO = JSON.parseObject(cartBussinessDTO.getBussinessBasicInfo(), LawReguInfoDTO.class);
                if(StringUtils.isBlank(lawReguInfoDTO.getFullName())){
                    lawReguList.add(new Pair<>(cartBussinessDTO.getBussinessId(), null));
                } else{
                    lawReguList.add(new Pair<>(cartBussinessDTO.getBussinessId(), lawReguInfoDTO.getFullName()));
                }
            } else if(BussinessCartType.JUDICIAL.name().equals(cartBussinessDTO.getBussinessType())){
                judicialList.add(cartBussinessDTO.getBussinessId());
            }
        }

        List<Judgement> judgementDownloads = null;
        List<LawRegu> lawReguDownloads = null;
        List<JudicialView> judicialDownloads = null;
        if(BussinessCartType.JUDGEMENT.name().equals(bussinessType)){
            judgementDownloads = getJudgementDownloads(judgementList);
        } else if((BussinessCartType.LAWREGU.name().equals(bussinessType))){
            lawReguDownloads = getLawReguDownloads(lawReguList);
        } else if(BussinessCartType.JUDICIAL.name().equals(bussinessType)){
            judicialDownloads = getJudicialDownloads(judicialList);
        } else if(BussinessCartType.REPORT.name().equals(bussinessType)){
            judgementDownloads = getJudgementDownloads(judgementList);
            lawReguDownloads = getLawReguDownloads(lawReguList);
            judicialDownloads = getJudicialDownloads(judicialList);
        }
        if(judgementDownloads != null && judgementList.size() != judgementDownloads.size()){
            log.error("案例数据有误");
            throw new CartServiceException("案例数据有误");
        } else if(lawReguDownloads != null && lawReguList.size() != lawReguDownloads.size()){
            log.error("法规数据有误");
            throw new CartServiceException("法规数据有误");
        } else if(judicialDownloads != null && judicialList.size() != judicialDownloads.size()){
            log.error("司法观点数据有误");
            throw new CartServiceException("司法观点数据有误");
        }

        String dataStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 定义检索时间
        String now = new SimpleDateFormat("yyyy年M月d日").format(new Date());

        // 根据type类型判断需要的下载类型
        // word
        if(DownloadType.DOWNLOAD_WORD.val().equals(type) || DownloadType.DOWNLOAD_WORD_EXCEL .val().equals(type)){

        }
        // excel
        if(DownloadType.DOWNLOAD_EXCEL.val().equals(type) || DownloadType.DOWNLOAD_WORD_EXCEL .val().equals(type)){
            // 案例
            if(BussinessCartType.JUDGEMENT.name().equals(bussinessType)){
                String excelName = tokenUser.getUserName() + "Alpha案例检索报告" + dataStr;
                // excel模板位置
                OPCPackage pkg = null;
                pkg = getOpcPackage(BussinessCartType.JUDGEMENT);
                XSSFWorkbook wb;
                try {
                    wb = new XSSFWorkbook(pkg);
                } catch (IOException e) {
                    log.error("excel工作区获取内容失败", e);
                    throw new CartServiceException("excel工作区获取内容失败", e);
                }
                XSSFSheet sheet = wb.getSheetAt(0);
                insertSheet(sheet, judgementDownloads, wb, now, excelName, BussinessCartType.JUDGEMENT);

                try {
                    FileOutputStream out = new FileOutputStream(
                            CartDownloadConstant.DOWNLOAD_EXCEL_PATH + excelName + ".xlsx");
                    wb.write(out);
                } catch (Exception e) {
                    log.error("excel生成失败", e);
                    throw new CartServiceException("excel生成失败", e);
                }
            }

            // 法规
            else if(BussinessCartType.LAWREGU.name().equals(bussinessType)){
                String excelName = tokenUser.getUserName() + "Alpha法规检索报告" + dataStr + ".xlsx";
                // excel模板位置
                OPCPackage pkg = null;
                pkg = getOpcPackage(BussinessCartType.LAWREGU);
                XSSFWorkbook wb;
                try {
                    wb = new XSSFWorkbook(pkg);
                } catch (IOException e) {
                    log.error("excel工作区获取内容失败", e);
                    throw new CartServiceException("excel工作区获取内容失败", e);
                }
                XSSFSheet sheet = wb.getSheetAt(0);
                insertSheet(sheet, lawReguDownloads, wb, now, excelName, BussinessCartType.LAWREGU);

                try {
                    FileOutputStream out = new FileOutputStream(
                            CartDownloadConstant.DOWNLOAD_EXCEL_PATH + excelName + ".xlsx");
                    wb.write(out);
                } catch (Exception e) {
                    log.error("excel生成失败", e);
                    throw new CartServiceException("excel生成失败", e);
                }
            }

            // 司法观点
            else if(BussinessCartType.JUDICIAL.name().equals(bussinessType)){
                String excelName = tokenUser.getUserName() + "Alpha观点检索报告" + dataStr + ".xlsx";
                // excel模板位置
                OPCPackage pkg = null;
                pkg = getOpcPackage(BussinessCartType.JUDICIAL);
                XSSFWorkbook wb;
                try {
                    wb = new XSSFWorkbook(pkg);
                } catch (IOException e) {
                    log.error("excel工作区获取内容失败", e);
                    throw new CartServiceException("excel工作区获取内容失败", e);
                }
                XSSFSheet sheet = wb.getSheetAt(0);
                insertSheet(sheet, judicialDownloads, wb, now, excelName, BussinessCartType.JUDICIAL);

                try {
                    FileOutputStream out = new FileOutputStream(
                            CartDownloadConstant.DOWNLOAD_EXCEL_PATH + excelName + ".xlsx");
                    wb.write(out);
                } catch (Exception e) {
                    log.error("excel生成失败", e);
                    throw new CartServiceException("excel生成失败", e);
                }
            }

            // 全部检索报告
            else{
                String excelName = tokenUser.getUserName() + "Alpha检索报告" + dataStr + ".xlsx";
                // excel模板位置
                OPCPackage pkg = null;
                pkg = getOpcPackage(BussinessCartType.REPORT);
                XSSFWorkbook wb;
                try {
                    wb = new XSSFWorkbook(pkg);
                } catch (IOException e) {
                    log.error("excel工作区获取内容失败", e);
                    throw new CartServiceException("excel工作区获取内容失败", e);
                }
                XSSFSheet sheet = wb.getSheetAt(0);
                insertSheet(sheet, judicialDownloads, wb, now, excelName, BussinessCartType.REPORT);

                try {
                    FileOutputStream out = new FileOutputStream(
                            CartDownloadConstant.DOWNLOAD_EXCEL_PATH + excelName + ".xlsx");
                    wb.write(out);
                } catch (Exception e) {
                    log.error("excel生成失败", e);
                    throw new CartServiceException("excel生成失败", e);
                }
            }

        }


    }

    private void insertLawReguSheet(XSSFSheet sheet, List<LawRegu> lawReguDownloads, XSSFWorkbook wb, String now, String excelName) {
        int lastRowNum = sheet.getLastRowNum();
        sheet.getRow(0).getCell(1).setCellValue(excelName);
        sheet.getRow(2).getCell(2).setCellValue(now);
        XSSFCellStyle idCellStyle = sheet.getRow(lastRowNum).getCell(1).getCellStyle();
        XSSFCellStyle contentCellStyle = sheet.getRow(lastRowNum).getCell(2).getCellStyle();
        XSSFCellStyle linkCellStyle = ExcelUtil.linkStyle(wb);
        short firstCellNum = sheet.getRow(lastRowNum).getFirstCellNum();
        short lastCellNum = sheet.getRow(lastRowNum).getLastCellNum();
        for (int i = 0; i <  lawReguDownloads.size(); i++) {
            LawRegu lawRegu = lawReguDownloads.get(i);
            XSSFRow lastRow = i <= 0 ? sheet.getRow(lastRowNum) : sheet.createRow(lastRowNum + i);
            lastRow.setHeightInPoints(110);
            ExcelUtil.createRowSetValueStyle(lastRow, lawRegu, firstCellNum, lastCellNum, i + 1, idCellStyle, contentCellStyle, linkCellStyle);
        }
    }

    private void insertJudgementSheet(XSSFSheet sheet, List<Judgement> judgementDownloads, XSSFWorkbook wb, String now, String excelName) {
        int lastRowNum = sheet.getLastRowNum();
        sheet.getRow(0).getCell(1).setCellValue(excelName);
        sheet.getRow(2).getCell(2).setCellValue(now);
        XSSFCellStyle idCellStyle = sheet.getRow(lastRowNum).getCell(1).getCellStyle();
        XSSFCellStyle contentCellStyle = sheet.getRow(lastRowNum).getCell(2).getCellStyle();
        XSSFCellStyle linkCellStyle = ExcelUtil.linkStyle(wb);
        short firstCellNum = sheet.getRow(lastRowNum).getFirstCellNum();
        short lastCellNum = sheet.getRow(lastRowNum).getLastCellNum();
        for (int i = 0; i <  judgementDownloads.size(); i++) {
            Judgement judgement = judgementDownloads.get(i);
            XSSFRow lastRow = i <= 0 ? sheet.getRow(lastRowNum) : sheet.createRow(lastRowNum + i);
            lastRow.setHeightInPoints(110);
            ExcelUtil.createRowSetValueStyle(lastRow, judgement, firstCellNum, lastCellNum, i + 1, idCellStyle, contentCellStyle, linkCellStyle);
        }
    }

    private void insertSheet(XSSFSheet sheet, List downloads, XSSFWorkbook wb, String now, String excelName, BussinessCartType bussinessCartType) {
        int lastRowNum = sheet.getLastRowNum();
        sheet.getRow(0).getCell(1).setCellValue(excelName);
        sheet.getRow(2).getCell(2).setCellValue(now);
        XSSFCellStyle idCellStyle = sheet.getRow(lastRowNum).getCell(1).getCellStyle();
        XSSFCellStyle contentCellStyle = sheet.getRow(lastRowNum).getCell(2).getCellStyle();
        XSSFCellStyle linkCellStyle = ExcelUtil.linkStyle(wb);
        short firstCellNum = sheet.getRow(lastRowNum).getFirstCellNum();
        short lastCellNum = sheet.getRow(lastRowNum).getLastCellNum();
        switch (bussinessCartType){
            case JUDGEMENT:
                for (int i = 0; i <  downloads.size(); i++) {
                    Judgement judgement = null;
                    try {
                        judgement = (Judgement) downloads.get(i);
                    } catch (Exception e) {
                        log.error("案例数据解析错误", e);
                        throw new CartServiceException("案例数据解析错误", e);
                    }
                    XSSFRow lastRow = i <= 0 ? sheet.getRow(lastRowNum) : sheet.createRow(lastRowNum + i);
                    lastRow.setHeightInPoints(110);
                    ExcelUtil.createRowSetValueStyle(lastRow, judgement, firstCellNum, lastCellNum, i + 1, idCellStyle, contentCellStyle, linkCellStyle);
                }
                break;
            case LAWREGU:
                for (int i = 0; i <  downloads.size(); i++) {
                    LawRegu lawRegu = null;
                    try {
                        lawRegu = (LawRegu) downloads.get(i);
                    } catch (Exception e) {
                        log.error("案例数据解析错误", e);
                        throw new CartServiceException("案例数据解析错误", e);
                    }
                    XSSFRow lastRow = i <= 0 ? sheet.getRow(lastRowNum) : sheet.createRow(lastRowNum + i);
                    lastRow.setHeightInPoints(110);
                    ExcelUtil.createRowSetValueStyle(lastRow, lawRegu, firstCellNum, lastCellNum, i + 1, idCellStyle, contentCellStyle, linkCellStyle);
                }
                break;
            case JUDICIAL:
                for (int i = 0; i <  downloads.size(); i++) {
                    JudicialView judicial = null;
                    try {
                        judicial = (JudicialView) downloads.get(i);
                    } catch (Exception e) {
                        log.error("案例数据解析错误", e);
                        throw new CartServiceException("案例数据解析错误", e);
                    }
                    XSSFRow lastRow = i <= 0 ? sheet.getRow(lastRowNum) : sheet.createRow(lastRowNum + i);
                    lastRow.setHeightInPoints(110);
                    ExcelUtil.createRowSetValueStyle(lastRow, judicial, firstCellNum, lastCellNum, i + 1, idCellStyle, contentCellStyle, linkCellStyle);
                }
                break;
            default:
                break;
        }
    }

    private OPCPackage getOpcPackage(BussinessCartType bussinessCartType) {
        String excelFileName = null;
        switch (bussinessCartType){
            case JUDGEMENT:
                excelFileName = CartDownloadConstant.DOWNLOAD_JUDGEMENT_EXCEL_TEMPLATE;
                break;
            case LAWREGU:
                excelFileName = CartDownloadConstant.DOWNLOAD_LAWREGU_EXCEL_TEMPLATE;
                break;
            case JUDICIAL:
                excelFileName = CartDownloadConstant.DOWNLOAD_JUDICIAL_EXCEL_TEMPLATE;
                break;
            case REPORT:
                excelFileName = CartDownloadConstant.DOWNLOAD_REPORT_EXCEL_TEMPLATE;
                break;
            default:
                break;
        }
        OPCPackage pkg;
        try {
            String excelModulePath = servletContext.getRealPath("/") + excelFileName;
            File file = new File(excelModulePath);
            if (!file.exists()) {
                log.error("批量下载功能的excel模板文件缺失...");
                throw new CartServiceException("批量下载功能的excel模板文件缺失...");
            }
            pkg = OPCPackage.open(file);
        } catch (InvalidFormatException e) {
            log.error("excel模版获取错误", e);
            throw new CartServiceException("excel模版获取错误", e);
        }
        return pkg;
    }

    private List<JudicialView> getJudicialDownloads(List<String> judicialList) {
        if(judicialList.size() <= 0){
            return null;
        }
        QueryBuilder query = QueryBuilders.termsQuery(CartConstant.JUDICIAL_VID, judicialList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDICIAL_INDICES).withTypes(CartConstant.JUDICIAL_TYPE)
                .withPageable(new PageRequest(0,judicialList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page page = elasticsearchTemplate.queryForPage(searchQuery, JudicialView.class);
        return page.getContent();
    }

    private List<LawRegu> getLawReguDownloads(List<Pair<String, String>> lawRegeList) {
        if(lawRegeList.size() <= 0){
            return null;
        }
        List<LawRegu> listTemp = new ArrayList<>();
        for (Pair pair:lawRegeList) {
            QueryBuilder query = QueryBuilders.termQuery(CartConstant.LAWREGU_LID, pair.getKey());
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                    .withIndices(CartConstant.LAWREGU_INDICES).withTypes(CartConstant.LAWREGU_TYPE)
                    .withPageable(new PageRequest(0,1));
            SearchQuery searchQuery = nativeSearchQueryBuilder.build();
            Page page = elasticsearchTemplate.queryForPage(searchQuery, LawRegu.class);
            try{
                LawRegu data = (LawRegu) page.getContent().get(0);
                if(pair.getValue() == null){
                    // 法规pair的value为null，将发条字段设置为空
                    data.setLaw_regulation_introductions_jsons(null);
                    data.setLaw_regulation_introductions(JSON.toJSONString(null));
                } else{
                    // 发条pair的value有值存在，将发条字段设置为对应发条值，其他数值排除不要，并将content内容更换为发条内容
                    List<LexiscnParegraphVo> introductions = data.getLaw_regulation_introductions_jsons();
                    if(introductions == null){
                        log.error("发条数据查询有误");
                        throw new CartException("发条数据查询有误");
                    }
                    for (LexiscnParegraphVo lexciscn:introductions) {
                        if(pair.getValue().equals(lexciscn.getFullName())){
                            List<LexiscnParegraphVo> tempIntroductions = new ArrayList<>();
                            tempIntroductions.add(lexciscn);
                            data.setLaw_regulation_introductions_jsons(tempIntroductions);
                            data.setLaw_regulation_introductions(JSON.toJSONString(tempIntroductions));
                            data.setContent(lexciscn.getFullName() + "\n" + lexciscn.getText());
                            data.setTitle(data.getTitle() + " " + lexciscn.getFullName());
                            break;
                        }
                    }
                }
                listTemp.add(data);
            } catch (Exception e){
                log.error("发条信息数据错误", e);
                throw new CartServiceException("发条信息数据错误", e);
            }
        }
        return listTemp;
    }

    private List<Judgement> getJudgementDownloads(List<String> judgementList) {
        if(judgementList.size() <= 0){
            return null;
        }
        QueryBuilder query = QueryBuilders.termsQuery(CartConstant.JUDGEMENT_JID, judgementList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDGEMENT_INDICES).withTypes(CartConstant.JUDGEMENT_TYPE)
                .withPageable(new PageRequest(0,judgementList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page page = elasticsearchTemplate.queryForPage(searchQuery, Judgement.class);
        return page.getContent();
    }

    private void checkBussinessType(String bussinessType) {
        if(StringUtils.isBlank(bussinessType) &&
                BussinessCartType.JUDGEMENT.name().equals(bussinessType) &&
                BussinessCartType.LAWREGU.name().equals(bussinessType) &&
                BussinessCartType.JUDICIAL.name().equals(bussinessType)){
            throw new CartServiceException("收藏类型不符合要求");
        }
    }

    private void checkType(Integer type) {
        if(!DownloadType.DOWNLOAD_WORD.val().equals(type) &&
                !DownloadType.DOWNLOAD_EXCEL.val().equals(type) &&
                !DownloadType.DOWNLOAD_WORD_EXCEL.val().equals(type)){
            throw new CartServiceException("下载类型不符合要求");
        };
    }

    private void checkData(List<CartBussinessVO> cartBussinessVOs, List<CartBussinessDTO> cartBussinessDTOs) {
        if(cartBussinessVOs.size() == cartBussinessDTOs.size()){
            return;
        }
        List<String> cartIds = new ArrayList<>();
        for (CartBussinessDTO cartBussinessDTO:cartBussinessDTOs) {
            cartIds.add(cartBussinessDTO.getBussinessId());
        }
        for (CartBussinessVO cartBussinessVO:cartBussinessVOs) {
            if(!cartIds.contains(cartBussinessVO.getBussinessId())){
                throw new CartServiceException(cartBussinessVO.getBussinessId() + "已被Alpha删除，具体原因请联系小橙子");
            };
        }
    }

    private void checkCartBussinessVOs(List<CartBussinessVO> cartBussinessVOs) {
        for (CartBussinessVO cartBussinessVO:cartBussinessVOs) {
            Assert.notNull(cartBussinessVO,"存储信息含有空值");
            if(!BussinessCartType.JUDGEMENT.name().equals(cartBussinessVO.getBussinessType()) &&
                    !BussinessCartType.LAWREGU.name().equals(cartBussinessVO.getBussinessType()) &&
                    !BussinessCartType.JUDICIAL.name().equals(cartBussinessVO.getBussinessType())){
                log.error("数据类型不符合");
                throw new CartServiceException("数据类型不符合");
            }
        }
        if(cartBussinessVOs.size() > CartConstant.CART_MAX_SAVE){
            log.error("最多添加200篇至检索报告");
            throw new CartServiceException("最多添加200篇至检索报告");
        }
        int count = this.getUserCartNumber();
        if(count + cartBussinessVOs.size() > CartConstant.CART_MAX_SAVE){
            String errorContent = "添加数量过多，还能添加" + (CartConstant.CART_MAX_SAVE - count) + "篇至检索报告";
            log.error(errorContent);
            throw new CartServiceException(errorContent);
        }

        // 校验数据库是否有重复信息
        List<String> jidList = new ArrayList<>();
        List<Pair<String, String>> lPairList = new ArrayList<>();
        List<String> vidList = new ArrayList<>();
        for (CartBussinessVO cartBussinessVO:cartBussinessVOs) {
            if(BussinessCartType.JUDGEMENT.name().equals(cartBussinessVO.getBussinessType())){
                jidList.add(cartBussinessVO.getBussinessId());
            }
            else if(BussinessCartType.LAWREGU.name().equals(cartBussinessVO.getBussinessType())){
                lPairList.add(new Pair<>(cartBussinessVO.getBussinessId(), cartBussinessVO.getLawReguItem()));
            }else if(BussinessCartType.JUDICIAL.name().equals(cartBussinessVO.getBussinessType())){
                vidList.add(cartBussinessVO.getBussinessId());
            }
        }
        CartBussinessListVO CartBussinessListVO = this.selectCartsByUser();
        if(jidList.size() > 0){
            for (JudgementVO judgementVO:CartBussinessListVO.getJudgementList()) {
                if(jidList.contains(judgementVO.getJid())){
                    String errorStr = "案例：" + judgementVO.getTitle() + "已经存在于检索报告中";
                    log.error(errorStr);
                    throw new CartServiceException(errorStr);
                }
            }
        }
        if(jidList.size() > 0){
            for (LawReguVO lawReguVO:CartBussinessListVO.getLawReguList()) {
                for (Pair<String, String> pair:lPairList) {
                    // 发条id相同且 (item均为空或者item相等)
                    if(lawReguVO.getLid().equals(pair.getKey())){
                        if(StringUtils.isBlank(lawReguVO.getFullName()) && StringUtils.isBlank(pair.getValue())){
                            String errorStr = "法规：" + lawReguVO.getTitle() + "已经存在于检索报告中";
                            log.error(errorStr);
                            throw new CartServiceException(errorStr);
                        }
                        else if(StringUtils.isNotBlank(lawReguVO.getFullName()) && StringUtils.isNotBlank(pair.getValue())
                                && lawReguVO.getFullName().equals(pair.getValue())){
                            String errorStr = "法条" + lawReguVO.getFullName() + "：" + lawReguVO.getTitle() + "已经存在于检索报告中";
                            log.error(errorStr);
                            throw new CartServiceException(errorStr);
                        }
                    }
                }
            }
        }
        if(vidList.size() > 0){
            for (JudicialVO judicialVO:CartBussinessListVO.getJudicialList()) {
                if(vidList.contains(judicialVO.getVid())){
                    String errorStr = "司法观点：" + judicialVO.getTitle() + "，已经存在于检索报告中";
                    log.error(errorStr);
                    throw new CartServiceException(errorStr);
                }
            }
        }

    }

    private void checkUser(TokenUser tokenUser) {
        Assert.notNull(tokenUser,"用户数据不存在");
        Assert.notNull(tokenUser.getUserId(),"用户数据有误");
        Assert.notNull(tokenUser.getOfficeId(),"律所数据有误");
    }

    private List<CartBussinessDTO> searchJudgement(List<String> judgementList, String userId, String officeId, Date now) {
        QueryBuilder query = QueryBuilders.termsQuery(CartConstant.JUDGEMENT_JID, judgementList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDGEMENT_INDICES).withTypes(CartConstant.JUDGEMENT_TYPE)
                .withPageable(new PageRequest(0,judgementList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page judgementPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);
        List<Map> content = judgementPage.getContent();
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        for (Map data:content) {
            if(data == null){
                log.error("内容已被Alpha删除，具体原因请联系小橙子");
                throw new CartServiceException("内容已被Alpha删除，具体原因请联系小橙子");
            }
            CartBussinessDTO cartBussinessDTO = new CartBussinessDTO();
            JudgementInfoDTO judgementInfoDTO = new JudgementInfoDTO();
            cartBussinessDTO.setUserId(userId);
            cartBussinessDTO.setOfficeId(officeId);
            cartBussinessDTO.setBussinessId((String) data.get(CartConstant.JUDGEMENT_JID));
            cartBussinessDTO.setBussinessType(BussinessCartType.JUDGEMENT.name());
            cartBussinessDTO.setBussinessStatus("0");
            cartBussinessDTO.setBussinessTitle((String)data.get(CartConstant.JUDGEMENT_TITLE));
            cartBussinessDTO.setScore(-1);
            judgementInfoDTO.setJid((String) data.get(CartConstant.JUDGEMENT_JID));
            judgementInfoDTO.setTitle((String) data.get(CartConstant.JUDGEMENT_TITLE));
            judgementInfoDTO.setCaseType((String) data.get(CartConstant.JUDGEMENT_LEVEL_CASE));
            judgementInfoDTO.setJudgementType((Integer) data.get(CartConstant.JUDGEMENT_CASE_TYPE) == 1 ? "裁决" : "判定");
            judgementInfoDTO.setCourtName((String) data.get(CartConstant.JUDGEMENT_COURT_NAME));
            judgementInfoDTO.setCaseNumber((String) data.get(CartConstant.JUDGEMENT_CASENUMBER));
            judgementInfoDTO.setJudgementDate((String) data.get(CartConstant.JUDGEMENT_JUDGEMENTINFO_DATE));
            Integer publishType = (Integer) data.get(CartConstant.JUDGEMENT_PUBLISH_TYPE);
            switch (publishType){
                case 0:
                    judgementInfoDTO.setPublicType("普通案例");
                    break;
                case 1:case 5:case 6:
                    judgementInfoDTO.setPublicType("公报案例");
                    break;
                case 2:case 3:case 4:
                    judgementInfoDTO.setPublicType("指导性案例");
                    break;
                default:
                    break;
            }
            Integer leveloftria = (Integer) data.get(CartConstant.JUDGEMENT_LEVELOFTRIA);
            switch (leveloftria){
                case 1:
                    judgementInfoDTO.setLeveloftria("一审");
                    break;
                case 2:
                    judgementInfoDTO.setLeveloftria("二审");
                    break;
                case 3:
                    judgementInfoDTO.setLeveloftria("再审");
                    break;
                case 4:
                    judgementInfoDTO.setLeveloftria("执行");
                    break;
                case -1:
                    judgementInfoDTO.setLeveloftria("其他");
                    break;
                default:
                    break;
            }
            Object paragraphs = data.get(CartConstant.JUDGEMENT_PARAGRAPHS);
            if(paragraphs != null && paragraphs instanceof List && ((List) paragraphs).size() > 0){
                Object text1 = ((List) paragraphs).get(0);
                if(text1 instanceof Map && ((Map) text1).get(CartConstant.JUDGEMENT_CONTENT) != null){
                    String innerContent = (String) ((Map) text1).get(CartConstant.JUDGEMENT_CONTENT);
                    cartBussinessDTO.setBussinessContent(splitContent(innerContent, CartConstant.DB_MAX_CONTENT));
                    judgementInfoDTO.setContent(splitContent(innerContent, CartConstant.CART_MAX_CONTENT));
                }
            }
            String bussinessBasicInfo = JSON.toJSONString(judgementInfoDTO);
            cartBussinessDTO.setBussinessBasicInfo(bussinessBasicInfo);
            cartBussinessDTO.setCstCreateTime(now);
            cartBussinessDTO.setCstUpdateTime(now);
            cartBussinessDTOs.add(cartBussinessDTO);
        }
        return cartBussinessDTOs;
    }

    private List<CartBussinessDTO> searchLawregu(List<Pair> lawreguList, String userId, String officeId, Date now) {
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        for (Pair pair:lawreguList) {
            if(pair.getKey() == null){
                log.error("包含错误数据");
                throw new CartServiceException("包含错误数据");
            }
            QueryBuilder query = QueryBuilders.termQuery(CartConstant.LAWREGU_LID, pair.getKey());
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                    .withIndices(CartConstant.LAWREGU_INDICES).withTypes(CartConstant.LAWREGU_TYPE)
                    .withPageable(new PageRequest(0,lawreguList.size()));
            SearchQuery searchQuery = nativeSearchQueryBuilder.build();
            Page lawreguPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);
            List<Map> content = lawreguPage.getContent();
            Map data = content.get(0);
            if(data == null){
                log.error("包含错误数据");
                throw new CartServiceException("包含错误数据");
            }
            CartBussinessDTO cartBussinessDTO = getCartBussinessDTO(userId, officeId, now, data, (String)pair.getValue());
            cartBussinessDTOs.add(cartBussinessDTO);
        }
        return cartBussinessDTOs;
    }

    private CartBussinessDTO getCartBussinessDTO(String userId, String officeId, Date now, Map data, String lawReguItem) {
        CartBussinessDTO cartBussinessDTO = new CartBussinessDTO();
        LawReguInfoDTO lawReguInfoDTO = new LawReguInfoDTO();
        cartBussinessDTO.setUserId(userId);
        cartBussinessDTO.setOfficeId(officeId);
        cartBussinessDTO.setBussinessId((String) data.get(CartConstant.LAWREGU_LID));
        cartBussinessDTO.setBussinessType(BussinessCartType.LAWREGU.name());
        cartBussinessDTO.setBussinessStatus("0");
        cartBussinessDTO.setBussinessTitle((String)data.get(CartConstant.LAWREGU_TITLE));
        cartBussinessDTO.setScore(-1);
        String innerContent = (String) data.get(CartConstant.LAWREGU_CONTENT);
        cartBussinessDTO.setBussinessContent(splitContent(innerContent, CartConstant.DB_MAX_CONTENT));
        lawReguInfoDTO.setLid((String) data.get(CartConstant.LAWREGU_LID));
        lawReguInfoDTO.setTitle((String) data.get(CartConstant.LAWREGU_TITLE));
        lawReguInfoDTO.setEffLevel((String) data.get(CartConstant.LAWREGU_EFF_LEVEL));
        lawReguInfoDTO.setTimeLimited((String) data.get(CartConstant.LAWREGU_TIME_LIMITED));
        lawReguInfoDTO.setDocumentNumber((String) data.get(CartConstant.LAWREGU_DOCUMENT_NUMBER));
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(data.get(CartConstant.LAWREGU_POSTING_DATA) != null){
                lawReguInfoDTO.setPostingDate(
                        sdf2.format(sdf1.parse(
                                (String) data.get(CartConstant.LAWREGU_POSTING_DATA)
                        ))
                );
            }
            if(data.get(CartConstant.LAWREGU_EFFECTIVE_DATA) != null){
                lawReguInfoDTO.setEffectiveDate(
                        sdf2.format(sdf1.parse(
                                (String) data.get(CartConstant.LAWREGU_EFFECTIVE_DATA)
                        ))
                );
            }
        } catch (Exception e) {
            log.error("格式化代码错误",e);
            throw new CartServiceException("格式化代码错误");
        }
        lawReguInfoDTO.setDispatchAuthority((String) data.get(CartConstant.LAWREGU_DISPATCH_AUTHORITY));
        lawReguInfoDTO.setContent(splitContent(innerContent, CartConstant.CART_MAX_CONTENT));
        //如果含有lawReguItem，则进行发条查询进行替换
        if(StringUtils.isNotBlank(lawReguItem)){
            Boolean hasItem = false;
            if(data.get(CartConstant.LAWREGU_INTRODUCTIONS) != null && data.get(CartConstant.LAWREGU_INTRODUCTIONS) instanceof List){
                List<Map> lawReguLationIntroductions = (List<Map>) data.get(CartConstant.LAWREGU_INTRODUCTIONS);
                for (Map introduction:lawReguLationIntroductions) {
                    String fullName = (String) introduction.get(CartConstant.LAWREGU_INTRODUCTIONS_FULL_NAME);
                    String introductionsContent = (String) introduction.get(CartConstant.LAWREGU_INTRODUCTIONS_TEXT);
                    if(StringUtils.isBlank(fullName)){
                        continue;
                    }
                    if(lawReguItem.equals(fullName)){
                        hasItem = true;
                        lawReguInfoDTO.setFullName(fullName);
                        lawReguInfoDTO.setContent(splitContent(introductionsContent, CartConstant.CART_MAX_CONTENT));
                        cartBussinessDTO.setBussinessContent(splitContent(introductionsContent, CartConstant.DB_MAX_CONTENT));
                    }
                }
            }
            if(!hasItem){
                String errorStr = data.get(CartConstant.LAWREGU_LID) + lawReguItem + "无法查询";
                log.error((String) data.get(errorStr));
                throw new CartServiceException(errorStr);
            }
        }

        String bussinessBasicInfo = JSON.toJSONString(lawReguInfoDTO);
        cartBussinessDTO.setBussinessBasicInfo(bussinessBasicInfo);
        cartBussinessDTO.setCstCreateTime(now);
        cartBussinessDTO.setCstUpdateTime(now);
        return cartBussinessDTO;
    }

    private List<CartBussinessDTO> searchJudicial(List<String> judicialList, String userId, String officeId, Date now) {
        QueryBuilder query = QueryBuilders.termsQuery(CartConstant.JUDICIAL_VID, judicialList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDICIAL_INDICES).withTypes(CartConstant.JUDICIAL_TYPE)
                .withPageable(new PageRequest(0,judicialList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page lawreguPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);
        List<Map> content = lawreguPage.getContent();
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        for (Map data:content) {
            if(data == null){
                log.error("包含错误数据");
                throw new CartServiceException("包含错误数据");
            }
            CartBussinessDTO cartBussinessDTO = new CartBussinessDTO();
            JudicialInfoDTO judicialInfoDTO = new JudicialInfoDTO();
            cartBussinessDTO.setUserId(userId);
            cartBussinessDTO.setOfficeId(officeId);
            cartBussinessDTO.setBussinessId((String) data.get(CartConstant.JUDICIAL_VID));
            cartBussinessDTO.setBussinessType(BussinessCartType.JUDICIAL.name());
            cartBussinessDTO.setBussinessStatus("0");
            cartBussinessDTO.setBussinessTitle((String)data.get(CartConstant.JUDICIAL_TITLE));
            cartBussinessDTO.setScore(-1);
            String innerContent = (String) data.get(CartConstant.JUDICIAL_CONTENT);
            cartBussinessDTO.setBussinessContent(splitContent(innerContent, CartConstant.DB_MAX_CONTENT));
            judicialInfoDTO.setVid((String) data.get(CartConstant.JUDICIAL_VID));
            judicialInfoDTO.setViewTitle((String) data.get(CartConstant.JUDICIAL_TITLE));
            String keywords = (String) data.get(CartConstant.JUDICIAL_KEYWORDS);
            if(StringUtils.isNotBlank(keywords)){
                List<String> keywordsArr = Splitter.on(" ").splitToList(keywords);
                judicialInfoDTO.setKeywordsArr(keywordsArr);
            }
            judicialInfoDTO.setContent(splitContent(innerContent, CartConstant.CART_MAX_CONTENT));
            String bussinessBasicInfo = JSON.toJSONString(judicialInfoDTO);
            cartBussinessDTO.setBussinessBasicInfo(bussinessBasicInfo);
            cartBussinessDTO.setCstCreateTime(now);
            cartBussinessDTO.setCstUpdateTime(now);
            cartBussinessDTOs.add(cartBussinessDTO);
        }
        return cartBussinessDTOs;
    }

    public static String splitContent(String content, int length){
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(content) && content.length() > length) {
            content = content.substring(0,length);
        }
        return content;
    }
}
