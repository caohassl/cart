package com.icourt.cart.entity.lawRegu;

import com.icourt.cart.utils.DateUtils;
import lombok.Data;

/**
 * 法规Vo,不包含内容段落等信息
 * 防止给前端信息过度冗余
 * Created by Caomr on 2018/2/28.
 */
@Data
public class LegislationHistoryVo {


    private String lid;

    private String title_format;

    /**
     * '法规标题名称'
     */
    private String title;

    private String history_titles;

    private String history_type;

    /**
     * 'title_term不分词字段'
     */
    private String title_term;

    /**
     * '发布时间'
     * 部分发布时间只精确到月
     */
    private String posting_date;

    public void setPosting_date(String posting_date){
        this.posting_date = DateUtils.parseDateYYYY_MM_dd(posting_date);

    }

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

    public void setEffective_date(String effective_date){
        this.effective_date = DateUtils.parseDateYYYY_MM_dd(effective_date);

    }

    private String effective_date_str;

    /**
     * '效力层级'
     */
    private String eff_level;

}
