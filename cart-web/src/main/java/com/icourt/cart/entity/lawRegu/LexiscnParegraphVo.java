package com.icourt.cart.entity.lawRegu;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 段落vo对象
 */
@Data
public class LexiscnParegraphVo {
    /**
     * 父节节点段落
     */
    private transient List<LexiscnParegraphVo> pPagregraphs = new ArrayList<>();
    /**
     * 全名称 第一章第一节
     */
    private String fullName;
    /**
     * 条款名称
     */
    private String name;
    /**
     * 原始文本
     */
    private String text;
    /**
     * 层级
     */
    private int level;
    /**
     * 当前序列
     */
    private int index;
    /**
     * 引用条数
     */
    private int referenceCount;

    /**
     * 恢复高亮给前端用
     */
    @JSONField(name = "isTitle")
    private boolean isTitle;
    @JSONField(name = "isPublisher")
    private boolean isPublisher;
    @JSONField(name = "isDate")
    private boolean isDate;
    @JSONField(name = "isIndicate")
    private boolean isIndicate;


}
