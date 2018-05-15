package com.icourt.cart.service;

import com.icourt.cart.vo.CartBussinessVO;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by Caomr on 2018/5/4.
 */
public interface CartService {

    Pair getUserCartNumber();

    void saveBussinessCart(List<CartBussinessVO> cartBussinessVOs);
}
