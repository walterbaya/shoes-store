package com.shoesstore.shoesstore.utils;

import java.util.ArrayList;
import java.util.List;

public class SaleForm {
    private String channel;
    private List<SaleItemForm> saleItems = new ArrayList<>();

    // Getters y Setters

    public String getChannel() {
        return channel;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public List<SaleItemForm> getSaleItems() {
        return saleItems;
    }
    public void setSaleItems(List<SaleItemForm> saleItems) {
        this.saleItems = saleItems;
    }


}
