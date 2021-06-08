package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class PriceHistory {
    private Calendar date;
    private BigDecimal price;

    public PriceHistory(Calendar date, BigDecimal price) {
        this.date = date;
        this.price = price;
    }

    public Date getDate() {
        return date.getTime();
    }

    public BigDecimal getPrice() {
        return price;
    }
}
