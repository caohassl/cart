package com.icourt.cart.vo;

import lombok.Data;

import java.util.List;

/**
 * Alpha 返回前端案例对象vo
 * <p>
 * Created by Duj on 2018/5/17.
 */
@Data
public class JudicialVO {
    /**
     *  主键id
     */
    int cartId;

    /**
     *  司法观点中的vid
     */
    String vid;

    /**
     *  收藏标题
     */
    String title;

    /**
     *  收藏内容
     */
    String fullContent;

    /**
     * judicialInfo信息
     */
    private List<String> keywordsArr;
    private String content;
}
