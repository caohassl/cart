package com.icourt.cart.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Alpha 检索购物车vo
 * <p>
 * Created by Caomr on 2018/5/4.
 */
@ApiModel
@Data
public class CartBussinessVO implements Serializable {


    @NotNull(message = "bussinessId不能为空")
    String bussinessId;
    @NotNull(message = "bussinessType不能为空")@Valid
    String bussinessType;

    String lawReguItem;


}

