package com.icourt.cart.dto;

import com.icourt.common.dao.Id;
import lombok.Data;

/**
 * Alpha 检索购物车dto
 * <p>
 * Created by Duj on 2018/5/13.
 */
@Data
public class CartBussinessDTO extends CartBaseDTO {

    /**
     * 主键id
     */
    @Id
    int CartId;

    /**
     * 用户编号
     */
    String UserId;

    /**
     *  律所编号
     */
    String OfficeId;

    /**
     *  对应收藏编号，案例中的jid，法规中的lid，司法观点中的vid
     */
    String bussinessId;

    /**
     *  收藏类型，JUDGEMENT代表案例，LAWREGU代表法规，JUDICIAL代表司法观点
     */
    String bussinessType;

    /**
     *  0-收藏，1一检索，2-删除
     */
    String bussinessStatus;

    /**
     * 收藏标题
     */
    String bussinessTitle;

    /**
     * 收藏内容
     */
    String bussinessContent;

    /**
     * 收藏文件基本信息
     */
    String bussinessBasicInfo;

    /**
     * 收藏分数，用于排序
     */
    int score;

}

