package com.icourt.cart.entity.judgement;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 段落vo对象
 *
 * @author lan
 */
@Data
public class SegmentsVO {

    /**
     * 标签类型
     */
    private int labelType;

    /**
     * 标签名称
     */
    private String lableName;

    /**
     * 分段长度
     */
    private long length;

    /**
     * 分断内容数据
     */
    private String text;

    /**
     * 子段落
     */
    private List<ParagraphVO> subParagraphs = new ArrayList<>();

}
