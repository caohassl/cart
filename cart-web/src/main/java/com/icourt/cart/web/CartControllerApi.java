package com.icourt.cart.web;

import com.icourt.cart.common.CartResponse;
import com.icourt.cart.service.CartService;
import com.icourt.cart.vo.CartBussinessListVO;
import com.icourt.cart.vo.CartBussinessVO;
import com.icourt.cart.vo.CartSortVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        int count = cartServiceImpl.getUserCartNumber();
        Pair pair = new Pair("count",count);
        CartResponse cartResponse = CartResponse.SUCCESS;
        cartResponse.setData(pair);
        return cartResponse;
    }

    @Override
    @ApiOperation(value = "", notes = "批量保存检索报告到购物车", httpMethod = "POST", response = CartResponse.class)
    @RequestMapping(value = "/saves", method = RequestMethod.POST)
    public CartResponse saveBussinessCarts(@ApiParam(name = "cartBussinessVOs", value = "需要保存的购物车list集合", required = true) @RequestBody(required = true) List<CartBussinessVO> cartBussinessVOs) {
        cartServiceImpl.saveBussinessCart(cartBussinessVOs);
        return CartResponse.SUCCESS;
    }

    @Override
    @ApiOperation(value = "", notes = "单篇保存检索报告到购物车", httpMethod = "POST", response = CartResponse.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CartResponse saveBussinessCart(@ApiParam(name = "cartBussinessVO", value = "需要保存的购物车数据", required = true) @RequestBody(required = true) CartBussinessVO cartBussinessVO) {
        List<CartBussinessVO> cartBussinessVOs = new ArrayList<>();
        cartBussinessVOs.add(cartBussinessVO);
        cartServiceImpl.saveBussinessCart(cartBussinessVOs);
        return CartResponse.SUCCESS;
    }

    @Override
    @ApiOperation(value = "", notes = "查询用户保存的检索报告购物车", httpMethod = "GET", response = CartResponse.class)
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public CartResponse searchUserCarts() {
        CartBussinessListVO cartBussinessListVO = cartServiceImpl.selectCartsByUser();
        return CartResponse.success(cartBussinessListVO);
    }

    @Override
    @ApiOperation(value = "", notes = "购物车排序", httpMethod = "POST", response = CartResponse.class)
    @RequestMapping(value = "/sort", method = RequestMethod.POST)
    public CartResponse sortBussinessCart(@ApiParam(name = "cartSortVO", value = "排序的list集合",required = true) @RequestBody(required = true) List<CartSortVO> cartSortVOs) {
        cartServiceImpl.updateCartScore(cartSortVOs);
        return CartResponse.SUCCESS;
    }

    @Override
    @ApiOperation(value = "", notes = "购物车批量删除", httpMethod = "GET", response = CartResponse.class)
    @RequestMapping(value = "/deletes", method = RequestMethod.GET)
    public CartResponse deleteByIds(@ApiParam(name = "cartBussinessIds", value = "批量删除",required = true) @RequestParam(value = "cartBussinessIds", required = true) List<Integer> cartBussinessIds) {
        cartServiceImpl.deleteByIds(cartBussinessIds);
        return CartResponse.SUCCESS;
    }

    @Override
    @ApiOperation(value = "", notes = "检索报告下载", httpMethod = "GET", response = CartResponse.class)
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public CartResponse downloadBussinessCart(
            @ApiParam(name = "type", value = "下载类型，1=word，2=excel，3=word+excel",required = true)
            @RequestParam(value = "type", required = true) Integer type,
            @ApiParam(name = "bussinessType", value = "收藏类型，JUDGEMENT代表案例，LAWREGU代表法规，JUDICIAL代表司法观点",required = false)
            @RequestParam(value = "bussinessType", required = false) String bussinessType) {
        cartServiceImpl.downloadBussinessCart(type, bussinessType);
        return CartResponse.SUCCESS;
    }


}