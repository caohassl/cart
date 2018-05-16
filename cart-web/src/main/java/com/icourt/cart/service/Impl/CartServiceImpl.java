package com.icourt.cart.service.Impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.icourt.cart.Enum.BussinessCartType;
import com.icourt.cart.config.CartConstant;
import com.icourt.cart.dao.CartDao;
import com.icourt.cart.dto.CartBussinessDTO;
import com.icourt.cart.dto.JudgementInfoDTO;
import com.icourt.cart.dto.JudicialInfoDTO;
import com.icourt.cart.dto.LawreguInfoDTO;
import com.icourt.cart.exception.CartServiceException;
import com.icourt.cart.service.CartService;
import com.icourt.cart.vo.CartBussinessVO;
import com.icourt.user.core.TokenUser;
import com.icourt.user.core.UserContext;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    @Override
    public Pair getUserCartNumber() {
        TokenUser tokenUser = UserContext.getUser();


        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");


        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();
        int userCartNumber = cartDao.getUserCartNumber(userId,officeId,"0");
        Pair pair = new Pair("userCartNumber",userCartNumber);
        return pair;
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
        List<String> lawreguList = new ArrayList<>();
        List<String> judicialList = new ArrayList<>();
        for (CartBussinessVO CartBussinessVO:cartBussinessVOs) {
            if(BussinessCartType.JUDGEMENT.name().equals(CartBussinessVO.getBussinessType())){
                judgementList.add(CartBussinessVO.getBussinessId());
            } else if(BussinessCartType.LAWREGU.name().equals(CartBussinessVO.getBussinessType())){
                lawreguList.add(CartBussinessVO.getBussinessId());
            } else if(BussinessCartType.JUDICIAL.name().equals(CartBussinessVO.getBussinessType())){
                judicialList.add(CartBussinessVO.getBussinessId());
            }
        }
        // 分别查询ES数据
        List<CartBussinessDTO> CartBussinessDTOs = new ArrayList<>();
        if(judgementList.size() > 0){
            CartBussinessDTOs.addAll(searchJudgement(judgementList, userId, officeId));
        }
        if(lawreguList.size() > 0){
            CartBussinessDTOs.addAll(searchLawregu(lawreguList, userId, officeId));
        }
        if(judicialList.size() > 0){
            CartBussinessDTOs.addAll(searchJudicial(judicialList, userId, officeId));
        }
        // 数据入库
        cartDao.insertCarts(CartBussinessDTOs);
        System.out.println(CartBussinessDTOs.size());
    }

    private void checkCartBussinessVOs(List<CartBussinessVO> cartBussinessVOs) {
        for (CartBussinessVO cartBussinessVO:cartBussinessVOs) {
            Assert.notNull(cartBussinessVO,"存储信息含有空值");
            if(!BussinessCartType.JUDGEMENT.name().equals(cartBussinessVO.getBussinessType()) &&
                    !BussinessCartType.LAWREGU.name().equals(cartBussinessVO.getBussinessType()) &&
                    !BussinessCartType.JUDICIAL.name().equals(cartBussinessVO.getBussinessType())){
                throw new CartServiceException("数据类型不符合");
            }
        }
        if(cartBussinessVOs.size() > CartConstant.CART_MAX_SAVE){
            throw new CartServiceException("数据超量");
        }
    }

    private void checkUser(TokenUser tokenUser) {
        Assert.notNull(tokenUser,"用户数据不存在");
        Assert.notNull(tokenUser.getUserId(),"用户数据有误");
    }

    private List<CartBussinessDTO> searchJudgement(List<String> judgementList, String userId, String officeId) {
        QueryBuilder query = QueryBuilders.termsQuery(CartConstant.JUDGEMENT_JID, judgementList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDGEMENT_INDICES).withTypes(CartConstant.JUDGEMENT_TYPE)
                .withPageable(new PageRequest(0,judgementList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page judgementPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);
        List<Map> content = judgementPage.getContent();
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        Date now = new Date();
        for (Map data:content) {
            if(data == null){
                log.error("包含错误数据");
                throw new CartServiceException("包含错误数据");
            }
            CartBussinessDTO cartBussinessDTO = new CartBussinessDTO();
            JudgementInfoDTO judgementInfoDTO = new JudgementInfoDTO();
            cartBussinessDTO.setUserId(userId);
            cartBussinessDTO.setOfficeId(officeId);
            cartBussinessDTO.setBussinessId((String) data.get(CartConstant.JUDGEMENT_JID));
            cartBussinessDTO.setBussinessType(BussinessCartType.JUDGEMENT.name());
            cartBussinessDTO.setBussinessStatus("0");
            cartBussinessDTO.setBussinessTitle((String)data.get(CartConstant.JUDGEMENT_TITLE));
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
                    cartBussinessDTO.setBussinessContent(innerContent.length() > CartConstant.DB_MAX_CONTENT ?
                            innerContent.substring(0,CartConstant.DB_MAX_CONTENT) : innerContent);
                    judgementInfoDTO.setContent(innerContent.length() > CartConstant.CART_MAX_CONTENT ?
                            innerContent.substring(0,CartConstant.CART_MAX_CONTENT) : innerContent);
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

    private List<CartBussinessDTO> searchLawregu(List<String> lawreguList, String userId, String officeId) {
        QueryBuilder query = QueryBuilders.termsQuery(CartConstant.LAWREGU_LID, lawreguList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.LAWREGU_INDICES).withTypes(CartConstant.LAWREGU_TYPE)
                .withPageable(new PageRequest(0,lawreguList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page lawreguPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);
        List<Map> content = lawreguPage.getContent();
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        Date now = new Date();
        for (Map data:content) {
            if(data == null){
                log.error("包含错误数据");
                throw new CartServiceException("包含错误数据");
            }
            CartBussinessDTO cartBussinessDTO = new CartBussinessDTO();
            LawreguInfoDTO lawreguInfoDTO = new LawreguInfoDTO();
            cartBussinessDTO.setUserId(userId);
            cartBussinessDTO.setOfficeId(officeId);
            cartBussinessDTO.setBussinessId((String) data.get(CartConstant.LAWREGU_LID));
            cartBussinessDTO.setBussinessType(BussinessCartType.LAWREGU.name());
            cartBussinessDTO.setBussinessStatus("0");
            cartBussinessDTO.setBussinessTitle((String)data.get(CartConstant.LAWREGU_TITLE));
            String innerContent = (String) data.get(CartConstant.LAWREGU_CONTENT);
            cartBussinessDTO.setBussinessContent(innerContent.length() > CartConstant.DB_MAX_CONTENT ?
                    innerContent.substring(0,CartConstant.DB_MAX_CONTENT) : innerContent);
            lawreguInfoDTO.setLid((String) data.get(CartConstant.LAWREGU_LID));
            lawreguInfoDTO.setTitle((String) data.get(CartConstant.LAWREGU_TITLE));
            lawreguInfoDTO.setEffLevel((String) data.get(CartConstant.LAWREGU_EFF_LEVEL));
            lawreguInfoDTO.setTimeLimited((String) data.get(CartConstant.LAWREGU_TIME_LIMITED));
            lawreguInfoDTO.setDocumentNumber((String) data.get(CartConstant.LAWREGU_DOCUMENT_NUMBER));
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if(data.get(CartConstant.LAWREGU_POSTING_DATA) != null){
                    lawreguInfoDTO.setPostingDate(
                        sdf2.format(sdf1.parse(
                                (String) data.get(CartConstant.LAWREGU_POSTING_DATA)
                        ))
                    );
                }
                if(data.get(CartConstant.LAWREGU_EFFECTIVE_DATA) != null){
                    lawreguInfoDTO.setEffectiveDate(
                        sdf2.format(sdf1.parse(
                                (String) data.get(CartConstant.LAWREGU_EFFECTIVE_DATA)
                        ))
                    );
                }
            } catch (Exception e) {
                log.error("格式化代码错误",e);
                throw new CartServiceException("格式化代码错误");
            }
            lawreguInfoDTO.setDispatchAuthority((String) data.get(CartConstant.LAWREGU_DISPATCH_AUTHORITY));
            lawreguInfoDTO.setContent(innerContent.length() > CartConstant.CART_MAX_CONTENT ?
                    innerContent.substring(0,CartConstant.CART_MAX_CONTENT) : innerContent);
            String bussinessBasicInfo = JSON.toJSONString(lawreguInfoDTO);
            cartBussinessDTO.setBussinessBasicInfo(bussinessBasicInfo);
            cartBussinessDTO.setCstCreateTime(now);
            cartBussinessDTO.setCstUpdateTime(now);
            cartBussinessDTOs.add(cartBussinessDTO);
        }
        return cartBussinessDTOs;
    }

    private List<CartBussinessDTO> searchJudicial(List<String> judicialList, String userId, String officeId) {
        QueryBuilder query = QueryBuilders.termsQuery(CartConstant.JUDICIAL_VID, judicialList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDICIAL_INDICES).withTypes(CartConstant.JUDICIAL_TYPE)
                .withPageable(new PageRequest(0,judicialList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page lawreguPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);
        List<Map> content = lawreguPage.getContent();
        List<CartBussinessDTO> cartBussinessDTOs = new ArrayList<>();
        Date now = new Date();
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
            String innerContent = (String) data.get(CartConstant.JUDICIAL_CONTENT);
            cartBussinessDTO.setBussinessContent(innerContent.length() > CartConstant.DB_MAX_CONTENT ?
                    innerContent.substring(0,CartConstant.DB_MAX_CONTENT) : innerContent);
            judicialInfoDTO.setVid((String) data.get(CartConstant.JUDICIAL_VID));
            judicialInfoDTO.setViewTitle((String) data.get(CartConstant.JUDICIAL_TITLE));
            String keywords = (String) data.get(CartConstant.JUDICIAL_KEYWORDS);
            if(StringUtils.isNotBlank(keywords)){
                List<String> keywordsArr = Splitter.on(" ").splitToList(keywords);
                judicialInfoDTO.setKeywordsArr(keywordsArr);
            }
            judicialInfoDTO.setContent(innerContent.length() > CartConstant.CART_MAX_CONTENT ?
                    innerContent.substring(0,CartConstant.CART_MAX_CONTENT) : innerContent);
            String bussinessBasicInfo = JSON.toJSONString(judicialInfoDTO);
            cartBussinessDTO.setBussinessBasicInfo(bussinessBasicInfo);
            cartBussinessDTO.setCstCreateTime(now);
            cartBussinessDTO.setCstUpdateTime(now);
            cartBussinessDTOs.add(cartBussinessDTO);
        }
        return cartBussinessDTOs;
    }
}
