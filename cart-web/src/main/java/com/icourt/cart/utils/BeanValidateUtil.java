package com.icourt.cart.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Caomr on 2018/5/4.
 */
public class BeanValidateUtil<T> {

    /**
     * 验证bean非空
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> String validate(T bean) {
        ValidatorFactory vFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = vFactory.getValidator();
        Set<ConstraintViolation<T>> set = validator.validate(bean);
        String errMsg = set.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
        return errMsg;
    }

//    public static String validateValue(String methodValue, String... limitValues) {
//        Class clazz = bean.getClass();
//        String methodValue =
//    }
}
