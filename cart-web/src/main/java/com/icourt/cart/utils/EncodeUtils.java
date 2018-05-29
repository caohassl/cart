package com.icourt.cart.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by liuconghui on 2017/7/17.
 */
public class EncodeUtils {
    public static String encodeURLWithoutTenChars(String s) {

        String str = null;

        try {
            str = URLEncoder.encode(s, "UTF-8");
            str = str.replaceAll("\\%2C", ",")
                    .replaceAll("\\%2F", "/")
                    .replaceAll("\\%3F", "?")
                    .replaceAll("\\%3A", ":")
                    .replaceAll("\\%40", "@")
                    .replaceAll("\\%26", "&")
                    .replaceAll("\\%3D", "=")
                    .replaceAll("\\+","%20")
                    .replaceAll("\\%2B", "+")
                    .replaceAll("\\%24", "$")
                    .replaceAll("\\%23", "#");
        } catch (Exception e) {
            throw new RuntimeException("调用EncodeUtils.encodeURLWithoutTenChars()方法出错！");
        }

        return str;
    }

    public static void main(String[] args) {
//        String str = "{[{\"name\":\"keyword\",\"value\":\"（2016）内0202民初3101号\"}], }";
        try {
            URLEncoder.encode(" +","utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
