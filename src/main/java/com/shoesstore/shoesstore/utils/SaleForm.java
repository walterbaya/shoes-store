package com.shoesstore.shoesstore.utils;

import com.shoesstore.shoesstore.model.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaleForm {
    private String channel;
    private List<SaleItemForm> saleItems = new ArrayList<>();
    private String paymentMethod;
    private double shippingCost;
    private double discountPercentage;
    private double total;
}
