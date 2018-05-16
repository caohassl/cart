package com.icourt.cart.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文书列表dto
 *
 * @author lan
 */
@Data
public class JudgementInfoDTO implements Serializable {

    /**
     * jid : eb611c68-1f24-4aef-82df-45575a607445
     * title : 原告铁岭市清河荣城物业有限公司诉被告刘宏物业服务合同纠纷一案一审民事判决书
     * caseType : 民事
     * judgementType : 判决
     * courtName : 铁岭市清河区人民法院
     * caseNumber : （2016）辽1204民初143号
     * judgementDate : 2016-11-11
     * publishDate : 2016-12-19
     * publishType : 0
     * leveloftria : 一审
     * content : 正文。。。120字以内
     */
    /**
     * 文书主键
     */
    private String jid;
    /**
     * 文书标题
     */
    private String title;
    /**
     * 案由类型
     */
    private String caseType = "民事";
    /**
     * 文书类型
     */
    private String judgementType = "判决";
    /**
     * 法院名称
     */
    private String courtName;
    /**
     * 案号
     */
    private String caseNumber;
    /**
     * 审判日期
     */
    private String judgementDate;
    /**
     * 案例类型: 0、普通案例 1、公报案例 2、指导性案例 3、最高法指导案例 4、最高检指导案例 5、最高法公报案例 6、最高检公报案例
     */
    private String publicType;
    /**
     * 裁决类型
     */
    private String leveloftria;
    /**
     * 主题内容(120字)
     */
    private String content;
}
