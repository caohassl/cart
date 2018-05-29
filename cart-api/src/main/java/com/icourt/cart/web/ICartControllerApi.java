package com.icourt.cart.web;

import com.icourt.cart.common.CartResponse;
import com.icourt.cart.vo.CartBussinessVO;
import com.icourt.cart.vo.CartSortVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
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

    CartResponse saveBussinessCarts(@ApiParam(name = "cartBussinessVOs", value = "需要保存的购物车list集合", required = true)
                                    @RequestBody(required = true) List<CartBussinessVO> cartBussinessVOs);

    CartResponse saveBussinessCart(@ApiParam(name = "cartBussinessVO", value = "需要保存的购物车数据", required = true)
                                   @RequestBody(required = true) CartBussinessVO cartBussinessVO);

    CartResponse searchUserCarts();

    CartResponse sortBussinessCart(@ApiParam(name = "cartSortVO", value = "排序的list集合",required = true)
                                   @RequestBody(required = true) List<CartSortVO> cartSortVOs);

    CartResponse deleteByIds(@ApiParam(name = "cartBussinessIds", value = "批量删除",required = true)
                             @RequestParam(value = "cartBussinessIds", required = true) List<Integer> cartBussinessIds);

    CartResponse downloadBussinessCart(
            @ApiParam(name = "type", value = "下载类型，1=word，2=excel，3=word+excel",required = true)
            @RequestParam(value = "type", required = true) Integer type,
            @ApiParam(name = "bussinessType", value = "收藏类型，JUDGEMENT代表案例，LAWREGU代表法规，JUDICIAL代表司法观点",required = false)
            @RequestParam(value = "bussinessType", required = true) String bussinessType);

}