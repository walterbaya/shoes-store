package com.shoesstore.shoesstore.model;

public enum PaymentCondition {
    EFECTIVO("Efectivo"),
    TRANSFERENCIA("Transferencia"),
    CHEQUE("Cheque"),
    CREDITO("Crédito");

    private final String displayName;

    PaymentCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}