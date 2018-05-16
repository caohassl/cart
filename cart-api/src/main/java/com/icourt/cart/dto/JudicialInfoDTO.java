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
public class JudicialInfoDTO implements Serializable {

    /**
     * vid : 00e8b8be9da1f062a1e1ef9d5d0e051a
     * viewTitle : 因婚姻家庭等民间纠纷激化引发的犯罪，被害人及其家属对被告人表示谅解的，应当作为酌定量刑情节
     * keywordsArr : [刑事附带民事诉讼, 民间纠纷, 被害人谅解]
     * content : 正文。。。120字以内
     */
    private String vid;
    private String viewTitle;
    private List<String> keywordsArr;
    private String content;
}
