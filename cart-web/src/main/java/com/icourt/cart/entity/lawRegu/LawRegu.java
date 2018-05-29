package com.icourt.cart.entity.lawRegu;

import com.icourt.cart.utils.CNNMFilter;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.ArrayList;
import java.util.List;


/**
 * 索引对象
 *
 * @author lilx
 */
@Document(indexName = "lawregu_dev", type = "law_regu")
@Data
@Mapping(mappingPath = "/mappings/lawregu-mappings.json")
public class LawRegu {

    /**
     * 法规标题
     */
    private String title_format;
    /*
    * 法规的md5 id,根据id字段生成
    * */
    @Id
    private String lid;

    /**
     * '原文url'
     */
    private String cap_url;

    /**
     * '法规标题名称'
     */
    private String title;

    /**
     * 立法沿革信息
     */
    private List<LegislationHistoryVo> legislationHistory;

    private String history_titles;

    private Integer history_type;

    public static String getHistoryType(){
        return "history_type";
    }
    public static String getHistoryTitles(){
        return "history_titles";
    }
    public static String getTitleTerm(){
        return "title_term";
    }
    /**
     * 'title_term不分词字段'
     */
    private String title_term;

    /**
     * 法规内容
     */
    private String content;

    /**
     * '发布时间'
     * 部分发布时间只精确到月
     */
    private String posting_date;

    private String posting_date_str;

    /**
     * 有效范围
     */
    private String effective_range;
    /**
     * 省市
     */
    private String province;
    /**
     * '发布机关'
     */
    private String dispatch_authority;
    /**
     * 文号
     */
    private String document_number;
    /**
     * '时效性'
     */
    private String time_limited;
    /**
     * '生效日期'
     * 部分生效日期只精确到月
     */
    private String effective_date;

    private String effective_date_str;

    /**
     * '分类'
     */
    private String classification;
    /**
     * 'lawid'
     */
    private String lawid;
    /**
     * 拆分段落
     */
    private String law_regulation_introductions;

    private List<LexiscnParegraphVo> law_regulation_introductions_jsons;

    /**
     * 目录结构
     */
    private String law_regulation_indexes;

    private List<LexiscnParegraphVo> law_regulation_indexes_jsons;

    /**
     * 高频法条
     */
    private List<LexiscnParegraphVo> top_instroductions_jsons;

    /**
     * 附件url
     */
    private String attachment_url;
    /**
     * 附件文件url
     */
    private String attachment_file_url;
    /**
     * '英文url'
     */

    private String ce_url;
    /**
     * '中文url'
     */
    private String cnce_url;
    /**
     * '效力层级'
     */
    private String eff_level;
    /**
     * '法规类型'
     */
    private int type;
/*    *//**
     * '更新时间'
     *//*
    private String update_time;*/
    /**
     * '标记'
     */
    private int flag;

    /**
     * 发布人姓名
     */
    private String post_persion_name;

    /**
     * 发布人职务
     */
    private String post_persion_duty;

    /**
     * 题注
     */
    private String credit_line;
    /**
     * 通过会议描述
     */
    private String pass_meeting;
    /**
     * 通过时间描述
     */
    private String pass_time;

    /**
     * 发文
     */
    private String indicate;

    private String tiaoTotalNumCN;

    public void setTiaoTotalNumCN(String tiaoTotalNumCN) {
        this.tiaoTotalNumCN = tiaoTotalNumCN;
        if(StringUtils.isNotEmpty(tiaoTotalNumCN)){
            this.tiaoTotalNum = CNNMFilter.cnNumericToArabic(tiaoTotalNumCN, true);
        }
    }

    private int tiaoTotalNum;

    /**
     * 内容末尾是否存在发文机关
     */
    private boolean exist_dispatch_authority;

    /**
     * 内容末尾是否存在发文时间
     */
    private boolean exist_posting_date;


    public static String getTitleName() {
        return "title";
    }

    public static String getTitleFormatName() {
        return "title_format";
    }

    public static String getLidName() {
        return "lid";
    }
    public static String getTimeLimitedName() {
        return "time_limited";
    }

    public static String getDocumentNumberName() {
        return "document_number";
    }

    public static String getContentName() {
        return "content";
    }

    public static String getPostingDateStrName() {
        return "posting_date_str";
    }

    public static String getEffectiveRangeName() {
        return "effective_range";
    }

    public static String getProvinceName() {
        return "province";
    }

    public static String getDispatchAuthorityName() {
        return "dispatch_authority";
    }

    public static String getDispatchAuthorityArrName() {
        return "dispatch_authority_arr";
    }

    public static String getEffectiveDateStrName() {
        return "effective_date_str";
    }

    public static String getEffLevelName() {
        return "eff_level";
    }

    public static String getPostingDateName() {
        return "posting_date";
    }

    public static String getEffectiveDateName() {
        return "effective_date";
    }

    public static String getLawRegulationIntroductionsName() {
        return "law_regulation_introductions";
    }

    public static String getLawRegulationIndexesName() {
        return "law_regulation_indexes";
    }

    public static String getPostPersionNameName() {
        return "post_persion_name";
    }

    public static String getPostPersionDutyName() {
        return "post_persion_duty";
    }

    public static String getCreditLineName() {
        return "credit_line";
    }

    public enum EffLevelEnum {
        FL("法律"),
        XZFG("行政法规"),
        SFJS("司法解释/文件"),
        BMGZ("部门规章/文件"),
        DFFG("地方法规/文件"),
        DFSFWJ("地方司法文件"),
        HYGD("行业规定"),
        TTGD("团体规定"),
        JSFG("军事法规");
        private final String value;

        private EffLevelEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * 获得效力等级list
         *
         * @return
         */
        public static List<String> getEffEnumList() {
            List<String> effList = new ArrayList<>();
            for (EffLevelEnum effLevelEnum : EffLevelEnum.values()) {
                effList.add(effLevelEnum.getValue());
            }
            return effList;
        }
    }

    public enum TimeLimitedEnum {
        XXYX("现行有效"),
        YBXD("已被修改"),
        ZQYJGHCG("征求意见稿或草案"),
        SWSX("尚未生效"),
        SX("失效");
        private final String value;

        private TimeLimitedEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * 获得排序list
         *
         * @return
         */
        public static List<String> getTimeLimitedEnumList() {
            List<String> timeList = new ArrayList<>();
            for (TimeLimitedEnum timeLimitedEnum : TimeLimitedEnum.values()) {
                timeList.add(timeLimitedEnum.getValue());
            }
            return timeList;
        }
    }
}
