package com.shoesstore.shoesstore.model.enums;

public enum ApplicationUserPermission {
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    SALE_READ("sale:read"),
    SALE_WRITE("sale:write"),
    SALE_DELETE("sale:delete"),
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    SUPPLIER_READ("supplier:read"),
    SUPPLIER_WRITE("supplier:write"),
    PURCHASE_ORDER_READ("purchase_order:read"),
    PURCHASE_ORDER_WRITE("purchase_order:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
