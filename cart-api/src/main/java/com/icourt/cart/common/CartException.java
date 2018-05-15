package com.icourt.cart.common;

/**
 * <b>Description:购物车处理异常类</b>
 * <p>
 * Created by Caomr on 2018/5/5.
 */
public class CartException extends Exception {

    public CartException() {
        super();
    }

    public CartException(String message) {
        super(message);
    }

    public CartException(String message, Throwable cause) {
        super(message, cause);
    }

    public CartException(Throwable cause) {
        super(cause);
    }

}
