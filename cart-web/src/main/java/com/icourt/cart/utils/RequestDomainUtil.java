package com.icourt.cart.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取域名
 *
 * @author Caomr
 */
public class RequestDomainUtil {

    public static ThreadLocal<String> domin = new ThreadLocal<String>();

    public static String getDomin() {
        return domin.get();
    }

    public static void setDomin(HttpServletRequest request, String context) {
        if (StringUtils.isEmpty(getDomin())) {
            String domain = request.getRequestURL().toString();
            domain = domain.substring(0, domain.indexOf(context)) + "/";
            domin.set(domain);
        }
    }

    public static void setDomin(String url) {
        if (!StringUtils.isEmpty(url)) {
            domin.set(url);
        }
    }


    public static void clear() {
        domin.remove();
    }
}