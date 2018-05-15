package com.icourt.cart.exception;

/**
 * @author dujiang
 */
public class CartServiceException extends RuntimeException {
    private static final long serialVersionUID = 6328080156962443814L;

    public CartServiceException() {
        super();
    }

    public CartServiceException(String message) {
        super(message);
    }

    public CartServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
