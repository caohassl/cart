package com.icourt.cart.entity.judgement;

import com.google.gson.annotations.Expose;
import lombok.Data;

/**
 * Created by lilx on 2017/7/27.
 */
@Data
public class LawreguFullNameBean {
    @Expose
    private String orginFullName;
    @Expose
    private String lawReguFullName;
}
