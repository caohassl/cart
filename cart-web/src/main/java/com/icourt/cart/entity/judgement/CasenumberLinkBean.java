package com.icourt.cart.entity.judgement;

import com.google.gson.annotations.Expose;
import lombok.Data;

/**
 * Created by liuconghui on 2017/7/15.
 */
@Data
public class CasenumberLinkBean {
    @Expose
    private String casenumber_str;
    @Expose
    private String jid;
}
