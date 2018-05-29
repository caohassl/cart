package com.icourt.cart.entity.judicialView;

import com.icourt.common.dao.Id;
import com.icourt.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;
import java.util.List;

/**
* @author: icourt
* @comments:  JudicialView模型
* @since Date： 2017-12-07 22:34:18
*/
@Data
@Document(indexName = "judicial_view_v5", type = "judicial_view")
@Mapping(mappingPath = "mappings/judicialview-mappings.json")
@Setting(settingPath = "mappings/judicialview-settings.json")
public class JudicialView extends BaseEntity{

    /**
     * 观点唯一id，根据edition_id+roll_id+view_id(版-卷-观点) md5值
     */
    @Id
    private String vid;

    /**
     * 观点序号
     */
    private String viewId;

    /**
     * 观点名称
     */
    private String viewTitle;

    /**
     * 图书版次id
     */
    private String editionId;

    /**
     * 图书版次说明
     */
    private String editionDesc;

    /**
     * 卷序号
     */
    private String rollId;

    /**
     * 卷名称
     */
    private String rollTitle;

    /**
     * 册序号
     */
    private String volumeId;

    /**
     * 册名称
     */
    private String volumeTitle;

    /**
     * 章序号
     */
    private String chapterId;

    /**
     * 章名称
     */
    private String chapterTitle;

    /**
     * 节序号
     */
    private String sectionId;

    /**
     * 节名称
     */
    private String sectionTitle;

    /**
     * 节子序号
     */
    private String subSectionId;

    /**
     * 节子名称
     */
    private String subSectionTitle;

    /**
     * 关键词
     */
    private String keywords;

	/**
     * 拆分段落,json格式[{"index":"1","level":"1","type":"摘要、正文、引用、注解","content","ext":"根据type扩展内容"}]
     */
    private String judicialViewParagraphs;

	/**
     * 目录结构,json格式[{"index":"1","fullName":"段落标题"}]
     */
    private String judicialViewIndexes;

	/**
     * 引用扩展,json格式[{"index":"1","quote":"引用正文","ref":"引用出处链接"}]
     */
    private String judicialViewQuotes;

    /**
     * 段落列表
     */
    private List<Paragraph> paragraphs;

	/**
     * 原始正文
     */
    private String content;

	/**
     * 页码
     */
    private String pageNum;

	/**
     * 著者说明
     */
    private String authorRemark;

	/**
     * 注解,json数组
     */
    private String annotation;

	/**
     * 创建时间
     */
    private Date gmtCreate;

	/**
     * 更新时间
     */
    private Date gmtUpdate;

    /**
     * 引用案例扩展,json数组格式
     */
    private String judicialViewQuoteCases;

    /**
     * 引用法规扩展,json数组格式
     */
    private String judicialViewQuoteLaws;

    /**
     * 观点序号排序
     */
    private Integer viewSort;

    /**
     * 不含注释的观点标题
     */
    private String viewTitleNoAnnotation;

    /**
     * 不含注释的关键词
     */
    private String keywordsNoAnnotation;

    /**
     * 引用法条概要信息
     */
    private List<LinkLawTiaoInfo> quoteLawTiaoList;

}
