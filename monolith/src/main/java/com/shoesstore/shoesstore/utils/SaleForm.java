package com.shoesstore.shoesstore.utils;

import com.shoesstore.shoesstore.model.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SaleForm {
    private String channel;
    private List<SaleItemForm> saleItems = new ArrayList<>();
    private String paymentMethod;
    private double shippingCost;
    private double discountPercentage;
    private double total;
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
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public double getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}
	public double getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
    
}
