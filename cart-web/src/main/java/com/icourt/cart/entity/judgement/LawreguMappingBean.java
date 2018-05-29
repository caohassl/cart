package com.icourt.cart.entity.judgement;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.List;

/**
 * Created by lilx on 2017/7/27.
 */
@Data
public class LawreguMappingBean {
    @Expose
    private List<MappingDetail> lawRegu;
    @Expose
    private List<MappingDetail> lawReguDetail;
}
