package com.icourt.cart.vo;

import com.icourt.cart.dto.CartBussinessDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Alpha 购物车检索返回前端对象vo
 * <p>
 * Created by Duj on 2018/5/17.
 */
@Data
public class CartBussinessListVO {
    List<JudgementVO> judgementList;
    List<LawReguVO> lawReguList;
    List<JudicialVO> judicialList;
    int judgementListCount;
    int lawReguListCount;
    int judicialListCount;
    int count;
}
