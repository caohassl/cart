package com.icourt.cart.Enum;

/**
 * Created by Caomr on 2018/5/17.
 *
 * 购物车检索报告类型的枚举类
 */
public enum DownloadType {
    // word
    DOWNLOAD_WORD(1),

    // excel
    DOWNLOAD_EXCEL(2),

    // word+excel
    DOWNLOAD_WORD_EXCEL(3);

    private DownloadType(Integer val){
        this.val = val;
    }

    private Integer val;

    public Integer val(){
        return val;
    }
}
