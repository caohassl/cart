package com.icourt.cart.common;


import com.icourt.core.Result;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Caomr on 2018/5/4.
 */
@Data
public class CartResponse<T> implements Serializable {

    public static final CartResponse SUCCESS = new CartResponse(true, "购物车请求成功");
    public static final CartResponse FAILURE = new CartResponse(false);

    private boolean isSuccess;

    private String resultMsg;

    private T data;

    public CartResponse(boolean isSuccess, String resultMsg, T data) {
        this.isSuccess = isSuccess;
        this.resultMsg = resultMsg;
        this.data = data;
    }

    public CartResponse(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public CartResponse(boolean isSuccess, String resultMsg) {
        this.isSuccess = isSuccess;
        this.resultMsg = resultMsg;
    }

    public static <T> CartResponse<T> success(T t) {
        CartResponse result = SUCCESS;
        result.setData(t);
        return result;
    }

    public static CartResponse failure(String resultMsg) {
        CartResponse result = FAILURE;
        result.setResultMsg(resultMsg);
        return result;
    }
}
