package com.icourt.cart.dao;

import com.icourt.cart.dto.CartBussinessDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Caomr on 2018/5/4.
 */
@Repository
public interface CartDao {

    int getUserCartNumber(@Param("userId") String userId, @Param("officeId") String officeId, @Param("status") String status);

    void insertCarts(@Param("cartBussinessDTOs") List<CartBussinessDTO> cartBussinessDTOs);
}
