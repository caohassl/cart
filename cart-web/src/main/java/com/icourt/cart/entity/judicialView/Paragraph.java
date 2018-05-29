package com.icourt.cart.entity.judicialView;

import lombok.Data;
import lombok.ToString;

/**
 * @author sus
 * @comments
 * @create 2017/12/7
 */
@Data
@ToString
public class Paragraph {
    private String name;
    private String content;
    private int index;
    private int level;
    private String ext;
    private int type;
    private int pId;
    /**
     * 原始法规正文
     */
    private int srcContent;
}
