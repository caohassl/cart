package com.icourt.cart.vo;

import lombok.Data;

/**
 * Alpha 返回前端案例对象vo
 * <p>
 * Created by Duj on 2018/5/17.
 */
@Data
public class JudgementVO {
    /**
     *  主键id
     */
    int cartId;

    /**
     *  案例中的jid
     */
    String jid;

    /**
     *  收藏标题
     */
    String title;

    /**
     *  收藏内容
     */
    String fullContent;

    /**
     * judgementInfo信息
     */
    private String caseType = "民事";
    private String judgementType = "判决";
    private String courtName;
    private String caseNumber;
    private String judgementDate;
    private String publicType;
    private String leveloftria;
    private String content;
}
