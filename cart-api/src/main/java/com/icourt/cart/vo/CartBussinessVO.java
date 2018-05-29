package com.icourt.cart.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Alpha 检索购物车vo
 * <p>
 * Created by Caomr on 2018/5/4.
 */
@ApiModel(value = "CartBussinessVO", description = "批量添加购物车请求请求信息")
@Data
public class CartBussinessVO implements Serializable {

    @ApiModelProperty(value = "业务ID")
    @NotNull(message = "bussinessId不能为空")
    String bussinessId;

    @ApiModelProperty(value = "业务类型，JUDGEMENT，LAWREGU或者JUDICIAL")
    @NotNull(message = "bussinessType不能为空")@Valid
    String bussinessType;

    @ApiModelProperty(value = "法规库条信息的fullName")
    String lawReguItem;


}

