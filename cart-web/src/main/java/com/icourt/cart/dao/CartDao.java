package com.icourt.cart.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * Created by Caomr on 2018/5/4.
 */
@Repository
public interface CartDao {

    @Select("select count(1) as num from bussiness_cart where user_id = #{userId} and office_id = #{officeId} and bussiness_status = #{status}")
    int getUserCartNumber(@Param("userId") String userId, @Param("officeId") String officeId, @Param("status") String status);
}
