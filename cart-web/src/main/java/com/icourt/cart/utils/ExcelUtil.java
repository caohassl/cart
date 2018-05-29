package com.icourt.cart.utils;
import com.icourt.cart.entity.judgement.Judgement;
import com.icourt.cart.entity.judgement.ParagraphVO;
import com.icourt.cart.entity.judgement.SegmentsVO;
import com.icourt.cart.entity.judicialView.JudicialView;
import com.icourt.cart.entity.lawRegu.LawRegu;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by xinhuiyang on 2017/6/29.
 */
public class ExcelUtil extends XSSFHyperlink {
    public static ThreadLocal<String> highLightStringThreadLocal = new ThreadLocal();



    public static String getHighLightStringThreadLocal() {
        return highLightStringThreadLocal.get();
    }

    public static void setHighLightStringThreadLocal(String highLightStringThreadLocal) {
        ExcelUtil.highLightStringThreadLocal.set(highLightStringThreadLocal);
    }

    public static void clearHighLightStringThreadLocal(){
        highLightStringThreadLocal.remove();
    }

    public ExcelUtil(HyperlinkType type) {
        super(type);
    }


    public static OPCPackage getOpcPackage(String excelModulePath) throws InvalidFormatException {
        OPCPackage pkg;
        File file = new File(excelModulePath);
        if (!file.exists()) {
            throw new RuntimeException("批量下载功能的excel模板文件缺失...");
        }
        pkg = OPCPackage.open(file);
        return pkg;
    }

    public static void updateRowValue(XSSFRow row, Judgement judgement, int rowId) {
        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();
        for (int i = firstCellNum; i < lastCellNum; i++) {
            XSSFCell rowCell = row.getCell(i);
            setCellValue(judgement, rowId, i, rowCell);
        }
    }

    private static void setCellValue(Object info, int rowId, int indexCell, XSSFCell rowCell) {
        // 填写judgement信息
        if(info instanceof Judgement){
            Judgement judgement = (Judgement) info;
            int levelLoftria = judgement.getAll_caseinfo_leveloftria();
            int publish_type = judgement.getPublish_type();
            switch (indexCell) {
                // 序号
                case 1:
                    rowCell.setCellValue(rowId);
                    break;
                // 案件名称
                case 2:
                    rowCell.setCellValue(judgement.getAll_caseinfo_casename());
                    break;
                // 链接
                case 3:
                    XSSFHyperlink link = new ExcelUtil(HyperlinkType.URL);
                    //excel中的链接跳转详情页，number跳重定向
                    rowCell.setCellType(CellType.FORMULA);
                    rowCell.setCellFormula("HYPERLINK(\"" + publicGetDetailUrlJudgement(judgement.getJid())+ "\",\"" + "前往Alpha"+ "\")");

                    link.setAddress(publicGetSeverUrlJudgement(judgement.getJid()));
                    rowCell.setHyperlink(link);
                    rowCell.setCellValue("前往Alpha");
                    break;
                // 审理法院
                case 4:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(judgement.getAll_caseinfo_court()));
                    break;
                // 案号
                case 5:
                    rowCell.setCellValue(judgement.getAll_caseinfo_casenumber());
                    break;
                // 案由
                case 6:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(judgement.getAll_text_cause()));
                    break;
                // 案件类型
                case 7:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(judgement.getLevel1_case()));
                    break;
                // 审理程序
                case 8:
                    String levelName = "其他";
                    if (levelLoftria == 1) {
                        levelName = "一审";
                    } else if (levelLoftria == 2) {
                        levelName = "二审";
                    } else if (levelLoftria == 3) {
                        levelName = "再审";
                    } else if (levelLoftria == 4) {
                        levelName = "执行";
                    }
                    rowCell.setCellValue(levelName);
                    break;
                // 文书类型
                case 9:
                    String judTypeName = "";
                    if (judgement.getType() == 1) {
                        judTypeName = "判决书";
                    } else if (judgement.getType() == 2) {
                        judTypeName = "裁定书";
                    }
                    rowCell.setCellValue(judTypeName);
                    break;
                // 当事人
                case 10:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(splitContent(getLableName("当事人信息", judgement),10000)));
                    break;
                // 法院认为
                case 11:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(splitContent(getCourtOption(publish_type, judgement),10000)));
                    break;
                // 裁判结果
                case 12:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(getCourtResult(publish_type, judgement)));
                    break;
                // 审判人员
                case 13:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(splitContent(getLableName("审判人员", judgement),10000)));
                    break;
                // 裁判时间
                case 14:
                    rowCell.setCellValue(judgement.getAll_judgementinfo_date());
                    break;
                default:
                    rowCell.setCellValue("未知");
                    break;
            }
        } else if(info instanceof LawRegu){
            LawRegu lawRegu = (LawRegu) info;
            switch (indexCell) {
                // 序号
                case 1:
                    rowCell.setCellValue(rowId);
                    break;
                // 法规名称
                case 2:
                    rowCell.setCellValue(lawRegu.getTitle());
                    break;
                // 链接
                case 3:
                    XSSFHyperlink link = new ExcelUtil(HyperlinkType.URL);
                    //excel中的链接跳转详情页，number跳重定向
                    rowCell.setCellType(CellType.FORMULA);
                    rowCell.setCellFormula("HYPERLINK(\"" + publicGetSeverUrlLawRegu(lawRegu.getLid())+ "\",\"" + "前往Alpha"+ "\")");

                    link.setAddress(publicGetSeverUrlLawRegu(lawRegu.getLid()));
                    rowCell.setHyperlink(link);
                    rowCell.setCellValue("前往Alpha");
                    break;
                // 发文机关
                case 4:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(lawRegu.getDispatch_authority()));
                    break;
                // 文号
                case 5:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(lawRegu.getDocument_number()));
                    break;
                // 发文日期
                case 6:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(lawRegu.getPosting_date_str()));
                    break;
                // 生效日期
                case 7:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(lawRegu.getEffective_date_str()));
                    break;
                // 效力级别
                case 8:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(lawRegu.getEff_level()));
                    break;
                // 条文内容
                case 9:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(lawRegu.getContent()));
                    break;
                // 备注
                case 10:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(lawRegu.getContent()));
                    break;
                default:
                    rowCell.setCellValue("未知");
                    break;
            }
        } else if(info instanceof JudicialView) {
            JudicialView judicial = (JudicialView) info;
            switch (indexCell) {
                // 序号
                case 1:
                    rowCell.setCellValue(rowId);
                    break;
                // 观点名称
                case 2:
                    rowCell.setCellValue(judicial.getViewTitle());
                    break;
                // 链接
                case 3:
                    XSSFHyperlink link = new ExcelUtil(HyperlinkType.URL);
                    //excel中的链接跳转详情页，number跳重定向
                    rowCell.setCellType(CellType.FORMULA);
                    rowCell.setCellFormula("HYPERLINK(\"" + publicGetSeverUrlJudicial(judicial.getVid()) + "\",\"" + "前往Alpha" + "\")");

                    link.setAddress(publicGetSeverUrlJudicial(judicial.getVid()));
                    rowCell.setHyperlink(link);
                    rowCell.setCellValue("前往Alpha");
                    break;
                // 关键词
                case 4:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(judicial.getKeywords()));
                    break;
                // 分卷
                case 5:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(judicial.getVolumeTitle()));
                    break;
                // 来源
                case 6:
                    String source = "《" + judicial.getEditionDesc() + "·" + judicial.getVolumeTitle() +
                            "》 第" + judicial.getPageNum() + "页";
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(source));
                    break;
                // 观点内容
                case 7:
                    rowCell.setCellValue(StringEscapeUtils.unescapeHtml(judicial.getContent()));
                    break;
                // 备注
                case 8:
                    rowCell.setCellValue("");
                    break;
                default:
                    rowCell.setCellValue("未知");
                    break;
            }
        }
    }

    public static String getLableName(String lableName, Judgement judgement) {
        List<SegmentsVO> paragraphs = judgement.getParagraphs();
        for (SegmentsVO paragraph:paragraphs) {
            if(lableName.equals(paragraph.getLableName()) && paragraph.getSubParagraphs() != null){
                String result = "";
                for (ParagraphVO subParagraph:paragraph.getSubParagraphs()) {
                    result = result + subParagraph.getText() + "\n";
                }
                return result;
            }
        }
        return "";
    }

    /**
     * 若内容太长则截取10000
     * @param content
     * @return
     */
    public static String splitTenThousand(String content){
        return splitContent(content, 10000);
    }

    public static String splitContent(String content, int length){
        if (StringUtils.isNotEmpty(content) && content.length() > length) {
            content = content.substring(0,length);
        }
        return content;
    }

    private static String publicGetSeverUrlJudicial(String vid) {
        String url = RequestDomainUtil.getDomin() + "#/app/tool/judicialView//detail/{" + vid + ", }";
        return url;
    }

    private static String publicGetSeverUrlLawRegu(String lid) {
        String url = RequestDomainUtil.getDomin() + "#/app/tool/lawsResult/{,}/detail/{" + lid + ", }";
        return url;
    }

    public static String publicGetDetailUrlJudgement(String jid) {
        String url = RequestDomainUtil.getDomin() + "#/app/tool/result/" + getVoidUrl() + "/detail/" + jid;
        return url;
    }

    public static String publicGetSeverUrlJudgement(String jid) {
        return getPublicVoidUrl(jid);
    }

    /**
     * 获得裁判结果字符串
     *
     * @param publish_type 案例类型:1=公报案例;2=指导案例
     * @param judgement
     * @return
     */
    public static String getCourtResult(int publish_type, Judgement judgement) {
        String result = "";
        switch (publish_type) {
            case 1:
                StringBuffer sbtype1 = new StringBuffer();
                result = sbtype1.append(getOptionByParag(judgement, "裁判结果")).append(getOptionByParag(judgement, "二审裁判结果")).append(getOptionByParag(judgement, "再审裁判结果")).toString();
                break;
            case 2:
                StringBuffer sbtype2 = new StringBuffer();
                result = sbtype2.append(getOptionByParag(judgement, "裁判结果")).append(getOptionByParag(judgement, "案件结果")).append(getOptionByParag(judgement, "终审判决")).append(getOptionByParag(judgement, "终审结果")).toString();
                break;
            default:
                result = getCommonOptionByKey("text_judgement", judgement);
                break;
        }

        return result;
    }


    /**
     * 获得本院认为字符串
     *
     * @param publish_type 案例类型:1=公报案例;2=指导案例
     * @param judgement
     * @return
     */
    public static String getCourtOption(int publish_type, Judgement judgement) {
        String option = "";
        switch (publish_type) {
            case 1:
                option = getOptionByParag(judgement, "本院认为");
                break;

            case 2:
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(getOptionByParag(judgement, "裁判理由")).append(getOptionByParag(judgement, "抗诉理由"));
                option = stringBuffer.toString();
                break;
            default:
                option = getCommonOptionByKey("text_opinion", judgement);
                break;
        }

        return option;
    }


    /**
     * 得到公报案例，指导案例（检例）的相应条目
     *
     * @param judgement
     * @param text
     * @return
     */
    private static String getOptionByParag(Judgement judgement, String text) {
        StringBuffer stringBuffer = new StringBuffer();
        List<SegmentsVO> segmentsVOS = judgement.getParagraphs();
        for (SegmentsVO segmentsVO : segmentsVOS) {
            if (text.equals(segmentsVO.getLableName())) {
                List<ParagraphVO> subParagraphs = segmentsVO.getSubParagraphs();
                for (ParagraphVO paragraphVO : subParagraphs) {
                    stringBuffer.append(paragraphVO.getText()).append("\n");
                }
            }
        }
        return stringBuffer.toString();
    }


    /**
     * 普通案例根据模糊的key得到法院认为,裁判结果
     *
     * @param key
     * @param judgement
     * @return
     */
    private static String getCommonOptionByKey(String key, Judgement judgement) {
        String[] orderList = judgement.getOrderList().replace("[", "").replace("]", "").split(",");
        String option = "";
        for (String methodStr : orderList) {
            methodStr=methodStr.replace("\"","");
            if (methodStr.contains(key)) {
                Class clazz = judgement.getClass();
                methodStr = "get" + methodStr.substring(0, 1).toUpperCase() + methodStr.substring(1);
                Method method = null;
                try {
                    method = clazz.getMethod(methodStr);
                    option = (String) method.invoke(judgement);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("未找到响应字段", e);
                }
            }
        }
        return option;
    }


    public static void createRowSetValueStyle(XSSFRow newRow, Object info, short firstCellNum, short lastCellNum, int rowId, XSSFCellStyle idCellStyle, XSSFCellStyle contentCellStyle, XSSFCellStyle linkCellStyle) {
        try {
            for (int i = firstCellNum; i < lastCellNum; i++) {
                XSSFCell newRowCell = newRow.createCell(i);
                if (i == firstCellNum) {
                    newRowCell.setCellStyle(idCellStyle);
                } else if (i == firstCellNum + 2) {
                    newRowCell.setCellStyle(linkCellStyle);
                } else {
                    newRowCell.setCellStyle(contentCellStyle);
                }
                setCellValue(info, rowId, i, newRowCell);
            }
        }catch (Exception ignore){
            //do nothing
        }

    }

    public static void renderExcelDownload(InputStream excelInStream, String excelModulePath, String excelName, ZipOutputStream out, byte[] buf) throws IOException {
        out.putNextEntry(new ZipEntry(excelName));
        //关闭流
        int bytesRead;
        while ((bytesRead = excelInStream.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
        }
        out.flush();
        out.closeEntry();
        excelInStream.close();
    }

    /**
     * <Strong>Description:</Strong> return the link style
     *
     * @param wookbook
     * @return
     * @author Caomr
     */
    public static XSSFCellStyle linkStyle(XSSFWorkbook wookbook) {
        XSSFCellStyle linkStyle = wookbook.createCellStyle();
        XSSFFont cellFont = wookbook.createFont();
        cellFont.setUnderline((byte) 1);
        cellFont.setColor(new XSSFColor(java.awt.Color.BLUE));
        linkStyle.setFont(cellFont);
        linkStyle.setAlignment(HorizontalAlignment.CENTER);
        linkStyle.setVerticalAlignment(VerticalAlignment.TOP);
        linkStyle.setWrapText(true);
        //下边框
        linkStyle.setBorderBottom(BorderStyle.THIN);
        return linkStyle;
    }

    public static String getPublicVoidUrl(String jid){
        String encodeQuery = getVoidUrl();
        return RequestDomainUtil.getDomin() + "ilaw/api/v1/es/redict/" + encodeQuery + "/" + jid;
    }

    public static String getVoidUrl(){
        Map<String, String> param = new HashMap<>();
        param.put("name", "keyword");
        param.put("value", "");
        String newQueryStr = JsonUtils.objToJson(param);
        String completeQueryStr = "{[" + newQueryStr + "], }";
        return EncodeUtils.encodeURLWithoutTenChars(completeQueryStr);
    }

    /**
     * 找到需要插入的行数，并新建一个POI的row对象
     * @param sheet
     * @param rowIndex
     * @return
     */
    public static XSSFRow createRow(XSSFSheet sheet, Integer rowIndex) {
        XSSFRow row = null;
        if (sheet.getRow(rowIndex) != null) {
            int lastRowNo = sheet.getLastRowNum();
            sheet.shiftRows(rowIndex, lastRowNo, 1);
        }
        row = sheet.createRow(rowIndex);
        return row;
    }
}
