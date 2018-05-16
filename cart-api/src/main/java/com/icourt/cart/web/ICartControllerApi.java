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
public interface ICartControllerApi {

    CartResponse getUserCartNumber();

    CartResponse saveBussinessCart(@ApiParam(name = "cartBussinessDto", value = "需要保存的购物车list集合",required = true) @RequestParam(value = "cartBussinessDto", required = true) List<CartBussinessVO> cartBussinessVOs);


}