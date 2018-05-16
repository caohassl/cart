package com.icourt.cart.config;

/**
 * @author dujiang
 */
public final class CartConstant {
    private CartConstant() {}

    /**
     * 数据传输最大量
     */
    public static int CART_MAX_SAVE = 200;

    /**
     * 数据库允许文本最大字数
     */
    public static int DB_MAX_CONTENT = 120;

    /**
     * 文本最大字数
     */
    public static int CART_MAX_CONTENT = 120;

    /**
     * 案例库地址
     */
    public static String JUDGEMENT_INDICES = "judgement_20180509-02";
    public static String JUDGEMENT_TYPE = "judgement";

    /**
     * 法规库地址
     */
    public static String LAWREGU_INDICES = "lawregu_sushi_v4";
    public static String LAWREGU_TYPE = "law_regu";

    /**
     * 司法观点库地址
     */
    public static String JUDICIAL_INDICES = "judicial_view_v5";
    public static String JUDICIAL_TYPE = "judicial_view";

    /**
     * 案例库数据
     */
    public static String JUDGEMENT_JID = "jid";
    public static String JUDGEMENT_TITLE = "all_caseinfo_casename";
    public static String JUDGEMENT_LEVEL_CASE = "level1_case";
    public static String JUDGEMENT_CASE_TYPE = "type";
    public static String JUDGEMENT_COURT_NAME = "all_caseinfo_court";
    public static String JUDGEMENT_CASENUMBER = "all_caseinfo_casenumber";
    public static String JUDGEMENT_JUDGEMENTINFO_DATE = "all_judgementinfo_date";
    public static String JUDGEMENT_PARAGRAPHS = "paragraphs";
    public static String JUDGEMENT_PUBLISH_TYPE = "publish_type";
    public static String JUDGEMENT_LEVELOFTRIA = "all_caseinfo_leveloftria";
    public static String JUDGEMENT_CONTENT = "text";

    /**
     * 法规库数据
     */
    public static String LAWREGU_LID = "lid";
    public static String LAWREGU_TITLE = "title";
    public static String LAWREGU_EFF_LEVEL = "eff_level";
    public static String LAWREGU_TIME_LIMITED = "time_limited";
    public static String LAWREGU_DOCUMENT_NUMBER = "document_number";
    public static String LAWREGU_POSTING_DATA = "posting_date_str";
    public static String LAWREGU_EFFECTIVE_DATA = "effective_date_str";
    public static String LAWREGU_DISPATCH_AUTHORITY = "dispatch_authority";
    public static String LAWREGU_CONTENT = "content";


    /**
     * 司法观点库数据
     */
    public static String JUDICIAL_VID = "vid";
    public static String JUDICIAL_TITLE = "viewTitle";
    public static String JUDICIAL_KEYWORDS = "keywords";
    public static String JUDICIAL_CONTENT = "content";
}
