package com.icourt.cart.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文书列表dto
 *
 * @author lan
 */
@Data
public class LawreguInfoDTO implements Serializable {

    /**
     * lid : d2914ce87bf7a6292bbe717f731cd610
     * title : 第十届全国人民代表大会第一次会议关于设立第十届全国人民代表大会专门委员会的决定
     * effLevel : 法律
     * timeLimited : 现行有效
     * documentNumber : 主席令[1999]第15号
     * postingDate : 2003-03-06
     * effectiveDate : 2003-03-06
     * dispatchAuthority : 人民代表大会
     * content : 正文。。。120字以内
     */
    private String lid;
    private String title;
    private String effLevel;
    private String timeLimited;
    private String documentNumber;
    private String postingDate;
    private String effectiveDate;
    private String dispatchAuthority;
    private String content;
}
