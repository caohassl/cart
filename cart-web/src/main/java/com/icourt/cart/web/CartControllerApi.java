package com.icourt.cart.web;

import com.icourt.cart.common.CartResponse;
import com.icourt.cart.service.CartService;
import com.icourt.cart.vo.CartBussinessVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 书检索controller-api外部接口实现类
 *
 * @author Caomr
 */
@RestController
@RequestMapping(value = "/api/v1/cart")
@Api(value = "cart-api", description = "检索报告购物车api", position = 1)
@Slf4j
public class CartControllerApi implements ICartControllerApi {

    @Resource
    CartService cartServiceImpl;

    @Override
    @ApiOperation(value = "", notes = "获取购物车信息数量", httpMethod = "GET", response = CartResponse.class)
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public CartResponse getUserCartNumber() {
        Pair part = cartServiceImpl.getUserCartNumber();
        CartResponse cartResponse = CartResponse.SUCCESS;
        cartResponse.setData(part);
        return cartResponse;
//        return CartResponse.SUCCESS;
    }

    @Override
    @ApiOperation(value = "", notes = "批量保存检索报告到购物车", httpMethod = "POST", response = CartResponse.class)
    @RequestMapping(value = "/saveList", method = RequestMethod.POST)
    public CartResponse saveBussinessCart(@ApiParam(name = "cartBussinessVOs", value = "需要保存的购物车list集合", required = true) @RequestBody(required = true) List<CartBussinessVO> cartBussinessVOs) {
        cartServiceImpl.saveBussinessCart(cartBussinessVOs);
        return CartResponse.SUCCESS;
    }


}