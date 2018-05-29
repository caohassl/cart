package com.icourt.cart.entity.judgement;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.ArrayList;
import java.util.List;


/**
 * 裁判文书对象
 *
 * @author lan
 */
@Document(indexName = "judgementsearch_test", type = "judgement")
@Data
@Mapping(mappingPath = "/mappings/judgement-mappings.json")
public class Judgement {


    /**
     * '文书唯一id，根据案号md5计算',
     */
    @Id
    private String jid;

    /**
     * 段落信息
     */
    private List<SegmentsVO> paragraphs = new ArrayList<>();


    private String thirdId;

    public void setThirdId(String thirdId) {
       this.thirdId = StringUtils.isNoneBlank(thirdId) ? thirdId : null;
    }

    /**
     * '文书标题'
     */
    private String all_caseinfo_casename;

    /**
     * '法院'
     */
    private String all_caseinfo_court;

    /**
     * 法院不分词
     */
    private String all_caseinfo_court_term;

    /**
     * '案号'
     */
    private String all_caseinfo_casenumber;

    /**
     * '审级  一审1；二审2；再审3'
     */
    private int all_caseinfo_leveloftria;
    /**
     * '案由'
     */
    private String all_text_cause;
    /**
     * '案由不分词'
     */
    private String all_text_cause_term;
    /**
     * '当事人信息'
     */
    private String all_text_litigantinfo;
    /**
     * '审判员信息'
     */
    private String all_text_judge;
    /**
     * '法律附件'
     */
    private String all_text_attachment;
    /**
     * '一审案件概述'
     */
    private String firstinstance_text_basicinfo;
    /**
     * '一审-原告-主张'
     */
    private String firstinstance_text_plaintiff_claim;
    /**
     * 一审-被告-主张
     */
    private String firstinstance_text_defendant_claim;
    /**
     * '一审-第三人-主张'
     */
    private String firstinstance_text_thirdparty_claim;
    /**
     * '一审-本院查明'
     */
    private String firstinstance_text_fact;
    /**
     * '一审-本院认为'
     */
    private String firstinstance_text_opinion;
    /**
     * '一审-裁判结果'
     */

    private String firstinstance_text_judgement;
    /**
     * '二审-案件概述'
     */

    private String secondinstance_text_basicinfo;
    /**
     * '二审-原审情况'
     */

    private String secondinstance_text_originaltrialinfo;
    /**
     * '二审-上诉人主张'
     */

    private String secondinstance_text_appellant_claim;
    /**
     * '二审-被上诉人主张'
     */

    private String secondinstance_text_appellee_claim;
    /**
     * '二审-一审法院查明'
     */

    private String secondinstance_text_firstinstance_fact;
    /**
     * '二审-一审法院认为'
     */

    private String secondinstance_text_firstinstance_opinion;
    /**
     * '二审-一审法院判决'
     */

    private String secondinstance_text_firstinstance_judgement;
    /**
     * '二审-法院查明'
     */

    private String secondinstance_text_fact;
    /**
     * '二审-法院认为'
     */

    private String secondinstance_text_opinion;
    /**
     * '二审-法院判决'
     */

    private String secondinstance_text_judgement;
    /**
     * '再审-案件概述'
     */

    private String retrial_text_basicinfo;
    /**
     * '再审-原一审情况'
     */

    private String retrial_text_originalfirsttrialinfo;
    /**
     * '再审-原二审情况'
     */

    private String retrial_text_originalsecondtrialinfo;

    /**
     * '再审-投诉机关主张'
     */

    private String retrial_text_procuratorate_claim;
    /**
     * '再审-申请人主张'
     */

    private String retrial_text_applicant_claim;
    /**
     * '再审-被申请人主张'
     */

    private String retrial_text_respondent_claim;
    /**
     * '正文_再审-本院查明'
     */

    private String retrial_text_fact;
    /**
     * '正文_再审-本院认为'
     */
    private String retrial_text_opinion;
    /**
     * '正文_再审-裁判结果'
     */

    private String retrial_text_judgement;
    /**
     * '文书日期'
     */
    private String all_judgementinfo_date;
    /**
     * '审判长'
     */

    private String all_chief_judge;
    /**
     * 审判长不分词
     */
    private String all_chief_judge_term;
    /**
     * '审判员'
     */
    private String all_judge;

    /**
     * 法官json数据
     */
    @Transient
    private String all_judges_json;
    /**
     * 审判员组合（all_judge、all_acting_judge、all_clerk）
     */
    private List<String> all_judges;
    /**
     * 法官+法院集合
     */
    private List<String> all_judge_courts;

    /**
     * 法官+法院集合，分词
     */
    private List<String> all_judge_courts_analyzed;
    /**
     * 法官+法院json数据
     */
    @Transient
    private String all_judge_courts_json;
    /**
     * '审判员不分词'
     */
    private String all_judge_term;
    /**
     * '代理审判员'
     */
    private String all_acting_judge;
    /**
     * '代理审判员不分词'
     */
    private String all_acting_judge_term;
    /**
     * '人民陪审员'
     */
    private String all_people_jury;
    /**
     * '书记员'
     */
    private String all_clerk;
    /**
     * '标记位'
     */
    private int flag;

    /**
     * 一级案由名称
     */
    private String level1_case;

    /**
     * 二级案由名称
     */
    private String level2_case;

    /**
     * 三级案由名称
     */
    private String level3_case;

    /**
     * 四级案由名称
     */
    private String level4_case;

    /**
     * 五级案由名称
     */
    private String level5_case;

    /**
     * 所有层级案由，分词
     */
    private String level_case_all_analyzed;

    /**
     * 最高人民法院
     */
    private String super_court;

    /**
     * 高级人民法院
     */
    private String higher_court;

    /**
     * 中级人民法院
     */
    private String intermediate_court;

    /**
     * 法院层级
     */
    private String court_level;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县区域
     */
    private String region;

    /**
     * 律师_律所
     */
    private List<String> lawyer_firm;

    /**
     * 律师_律所，分词
     */
    private List<String> lawyer_firm_analyzed;

    /**
     * 律师
     */
    private List<String> lawyer_term;

    /**
     * 当事人json数据
     */
    @Transient
    private String all_litigant_json;

    /**
     * 当事人集合
     */
    private List<String> all_litigant;

    /**
     * 当事人集合，分词
     */
    private List<String> all_litigant_analyzed;

    /**
     * 律所集合
     */
    private List<String> lawfirm_term;

    /**
     * 律所集合，分词
     */
    private List<String> lawfirm_analyzed;

    /**
     * 原告
     */
    private List<String> plaintiff;

    /**
     * '被告'
     */
    private List<String> defendant;

    /**
     * '第三人'
     */
    private List<String> thirdparty;

    /**
     * '上诉人'
     */
    private List<String> appellant;

    /**
     * '被上诉人'
     */
    private List<String> appellee;

    /**
     * '再审申请人'
     */
    private List<String> applicant;

    /**
     * 身份类型（原告、被告）
     */
    private List<String> identity;

    /**
     * '再审被申请人'
     */
    private List<String> respondent;

    /**
     * '其他当事人'
     */
    private List<String> otherpary;

    /**
     * 提取关联合同contract列表
     */
    private List<String> contracts;

    /**
     * 提取关联合同contract列表(文本内容)
     */
    private String contract;

    /**
     * 受理费用
     */
    private Double acceptance_fee;

    /**
     * 排序规则
     */
    private String orderList;

    /**
     * 文书类型
     */
    private Integer type;

    /**
     * 标的级别
     */
    private Integer acceptance_fee_level;

    /**
     * 标的费用
     */
    private Integer claim_value;

    /**
     * 引用法规json数据
     */
//    private String law_regu_json;

    /**
     * 引用法规详情
     */
//    private String law_regu_detail_json;

    /**
     * 引用法规名称列表
     */
//    private List<String> law_regu_jsons;

    /**
     * 引用法规法条列表
     */
//    private List<String> law_regu_detail_jsons;

    /**
     * 引用法规法条原文
     */
    private List<String> law_regu_detail_origin_jsons;

    /**
     * 引用法条列表
     */
    private List<LawReguLinkBean> law_regu_details;

    private String law_regu_mapping_json;

    private LawreguMappingBean law_regu_mapping_jsons;

    private String law_regu_origin_mapping_json;

    private List<LawreguFullNameBean> law_regu_origin_mapping_jsons;

    /**
     * 案号关联json
     */
    private String casenumber_json;

    /**
     * 案号关联list
     */
    private List<CasenumberLinkBean> casenumber_link_bean_list;

    private String key_words;

    private List<String> key_words_term;

    private String sight_words;

    private List<String> sight_words_term;

    private String dispute_focus;

    private String evidence;

    /**
     * 案例类型
     */
    private Integer publish_type;

    /**
     * 来源分类
     */
    private Integer source_type;

    /**
     * 执行长
     */
    private String all_chief_executor;
    /**
     * 执行员
     */
    private String all_executor;
    /**
     * 当事人-公司
     */
    private List<String> litigant_company;
    /**
     * 法官助理
     */
    private String all_deputy;

    /**
     * 公诉机关
     */
    private List<String> prosecution_organ;
    /**
     * 公诉机关
     */
    private List<String> prosecution_organ_term;

    /**
     * 庭审视频链接
     */
    private String tingshen_video_link;

}
