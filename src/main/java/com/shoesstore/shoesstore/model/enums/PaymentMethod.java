package com.shoesstore.shoesstore.model.enums;

public enum PaymentMethod {
    EFECTIVO("Efectivo"),
    TRANSFERENCIA("Transferencia"),
    CHEQUE("Cheque"),
    CREDITO("Crédito");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}