package com.shoesstore.shoesstore.events;

import com.shoesstore.shoesstore.model.Sale;
import org.springframework.context.ApplicationEvent;

public class SaleCreatedEvent extends ApplicationEvent {

    private final Sale sale;

    public SaleCreatedEvent(Object source, Sale sale) {
        super(source);
        this.sale = sale;
    }

    public Sale getSale() {
        return sale;
    }
}
