package com.shoesstore.shoesstore.mapper;

import com.shoesstore.shoesstore.dto.PurchaseOrderDto;
import com.shoesstore.shoesstore.dto.PurchaseOrderItemDto;
import com.shoesstore.shoesstore.model.PurchaseOrder;
import com.shoesstore.shoesstore.model.PurchaseOrderItem;
import com.shoesstore.shoesstore.model.Product;

public class PurchaseOrderMapper {

    public static PurchaseOrder convertToEntity(PurchaseOrderDto dto) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(dto.getId());
        // Supplier se setea en el servicio
        order.setDispatchDate(dto.getDeliveryDate()); // Mapeo de deliveryDate a dispatchDate
        order.setPriorityCondition(dto.getPriorityCondition());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setDiscount(dto.getDiscount());
        order.setShippingCost(dto.getShippingCost());
        order.setNotes(dto.getNotes());
        // Items se setean en el servicio
        return order;
    }

    public static PurchaseOrderItem convertToEntityItem(PurchaseOrderItemDto dto, Product product) {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPurchasePrice(dto.getPurchasePrice());
        return item;
    }

}
