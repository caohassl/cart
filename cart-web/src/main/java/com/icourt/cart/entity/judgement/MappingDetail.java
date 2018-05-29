package com.icourt.cart.entity.judgement;

import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class MappingDetail {
    @Expose
    private String extraFullName;
    @Expose
    private String lawReguDetailFullName;
    @Expose
    private String lawReguName;
    @Expose
    private String indexInfo;
    @Expose
    private String lid;
    @Expose
    private String lawReguType;
    @Expose
    private String lawReguDetailToItemName;
}
