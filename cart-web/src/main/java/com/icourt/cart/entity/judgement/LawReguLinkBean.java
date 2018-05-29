package com.icourt.cart.entity.judgement;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * 法条信息bean
 */
@Data
public class LawReguLinkBean {
    /**
     * 案例原文中的法规及法条全名称 《xxx法》第xxx条款项
     */
    @Expose
    private String originFullName;

    /**
     * 案例原文中的法规名称 《xxx法》
     */
    @Expose
    private String originLawNameWithSymbol;

    /**
     * 案例原文中的法条名称 xxx条款项
     */
    @Expose
    private String originItemName;

    /**
     * 法规库中的法规及法条全名称 xxx法xxx条款项
     */
    @Expose
    private String fullName;

    /**
     * 法规库中法规名称(带书名号) 《xxx》
     */
    @Expose
    private String lawNameWithSymbol;

    @Expose
    private String lawName;

    /**
     * 法规库中条款项名称 xxx条款项
     */
    @Expose
    private String itemName;

    /**
     * 法规库中的条名称， 只关联定位到条的粒度
     */
    @Expose
    private String tiaoName;

    /**
     * 当前定位的法规库中的内容
     */
    private String content;

    /**
     * 当前定位的条层级
     */
    @Expose
    private Integer level;

    /**
     * 当前定位的条位置
     */
    @Expose
    private Integer index;

    /**
     * 条款项被案例引用次数
     */
    @Expose
    private int totalRef;

    @Expose
    private String lid;

    public void setLawName(String lawName) {
        this.lawName = lawName;
        if (StringUtils.isNotBlank(lawName)) {
            this.lawNameWithSymbol = "《" + lawName + "》";
        }
    }

    /**
     * 有特殊字符，不能用replaceFirst
     *
     * @return
     */
    public String getOriginItemName() {
        if (StringUtils.isNotBlank(originFullName) && StringUtils.isNotBlank(originLawNameWithSymbol)) {
            String tempPre = originFullName;
            String tempSuff = originFullName;
            int pos = originFullName.indexOf(originLawNameWithSymbol);
            if (pos != -1) {
                return tempPre.substring(0, pos) + tempSuff.substring(pos + originLawNameWithSymbol.length(), originFullName.length());
            }
            return originFullName;
        } else {
            return "";
        }
    }

}
