package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.general.AbstractEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AbstractEntity implements Serializable {
    private List<CartItem> items;

    private int totalQuantity;

    private BigDecimal totalCost;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
