package com.icourt.cart.dao;

import com.icourt.cart.dto.CartBussinessDTO;
import com.icourt.cart.vo.CartSortVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Caomr on 2018/5/4.
 */
public interface CartDao {

    int getUserCartNumber(@Param("userId") String userId, @Param("officeId") String officeId, @Param("status") String status);

    void insertCarts(@Param("cartBussinessDTOs") List<CartBussinessDTO> cartBussinessDTOs);

    List<CartBussinessDTO> selectCartsByUser(@Param("userId") String userId, @Param("officeId") String officeId,
                                             @Param("status") String status, @Param("bussinessType") String bussinessType);

    void updateCartScore(@Param("cartId") Integer cartId, @Param("userId") String userId,
                         @Param("officeId") String officeId, @Param("sort") Integer sort);

    void updateCartBussinessStatus(@Param("cartId") Integer cartId, @Param("userId") String userId,
                                   @Param("officeId") String officeId, @Param("bussinessStatus") String bussinessStatus);
}
