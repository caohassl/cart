package com.icourt.cart.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Alpha basedto
 * <p>
 * Created by Duj on 2018/5/13.
 */
@Data
public class CartBaseDTO implements Serializable {
    /**
     * 主键id
     */
    Date cstCreateTime;

    /**
     * 用户编号
     */
    Date cstUpdateTime;
}
