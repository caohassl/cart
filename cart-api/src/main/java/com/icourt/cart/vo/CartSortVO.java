package com.icourt.cart.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Alpha 检索购物车vo
 * <p>
 * Created by Caomr on 2018/5/4.
 */
@ApiModel(value = "CartSortVO", description = "排序请求信息")
@Data
public class CartSortVO {

    @ApiModelProperty(value = "排序字段")
    @NotNull
    Integer sort;

    @ApiModelProperty(value = "购物车主键id")
    @NotNull
    Integer cartId;
}
