package com.icourt.cart.service;

import com.icourt.cart.vo.CartBussinessListVO;
import com.icourt.cart.vo.CartBussinessVO;
import com.icourt.cart.vo.CartSortVO;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by Caomr on 2018/5/4.
 */
public interface CartService {

    int getUserCartNumber();

    void saveBussinessCart(List<CartBussinessVO> cartBussinessVOs);

    CartBussinessListVO selectCartsByUser();

    void updateCartScore(List<CartSortVO> cartSortVOs);

    void deleteByIds(List<Integer> cartBussinessIds);

    void downloadBussinessCart(Integer type, String bussinessType);
}
