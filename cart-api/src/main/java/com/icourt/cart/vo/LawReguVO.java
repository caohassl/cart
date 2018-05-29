package com.icourt.cart.vo;

import lombok.Data;

/**
 * Alpha 返回前端案例对象vo
 * <p>
 * Created by Duj on 2018/5/17.
 */
@Data
public class LawReguVO {
    /**
     *  主键id
     */
    int cartId;

    /**
     *  法规中的lid
     */
    String lid;

    /**
     *  收藏标题
     */
    String title;

    /**
     *  收藏内容
     */
    String fullContent;

    /**
     * lawReguInfo信息
     */
    private String effLevel;
    private String timeLimited;
    private String documentNumber;
    private String postingDate;
    private String effectiveDate;
    private String dispatchAuthority;
    private String content;
    private String fullName;
}
