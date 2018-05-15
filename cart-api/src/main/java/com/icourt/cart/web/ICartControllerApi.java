package com.icourt.cart.web;

import com.icourt.cart.common.CartResponse;
import com.icourt.cart.vo.CartBussinessVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * 购物车controllerApi
 *
 * @author Caomr
 */
@RequestMapping("api/v1/cart")
@Api(value = "api/v1/cart", description = "购物车api", position = 1)
public interface ICartControllerApi {

    @ApiOperation(value = "", notes = "购物车的数量", httpMethod = "GET", response = CartResponse.class)
    @RequestMapping(value = "/getUserCartNumber", method = RequestMethod.GET)
    CartResponse getUserCartNumber();

    @ApiOperation(value = "", notes = "保存到购物", httpMethod = "POST", response = CartResponse.class)
    @RequestMapping(value = "/saveBussinessCart", method = RequestMethod.POST)
    CartResponse saveBussinessCart(@ApiParam(name = "cartBussinessDto", value = "需要保存的购物车list集合",required = true) @RequestParam(value = "cartBussinessDto", required = true) List<CartBussinessVO> cartBussinessVOs);


}