package com.es.phoneshop.model.product.dao.searchparam.advancedsearch;

import java.math.BigDecimal;

public class AdvancedParam {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private String desc;

    private DescParam descParam;

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDescParam(DescParam descParam) {
        this.descParam = descParam;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public String getDesc() {
        return desc;
    }

    public DescParam getDescParam() {
        return descParam;
    }
}
