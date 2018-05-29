package com.icourt.cart.entity.judicialView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author sus
 * @comments
 * @create 2017/12/7
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LinkLawTiaoInfo {
    /**
     * 法规名称
     */
    private String lawName;
    /**
     * 法规id
     */
    private String lid;

    /**
     * 法条名称
     */
    private String tiaoName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LinkLawTiaoInfo that = (LinkLawTiaoInfo) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(lawName, that.lawName)
                .append(lid, that.lid)
                .append(tiaoName, that.tiaoName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(lawName)
                .append(lid)
                .append(tiaoName)
                .toHashCode();
    }
}
