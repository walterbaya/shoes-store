package com.shoesstore.shoesstore.model.enums;

public enum Gender {
    HOMBRE("Hombre"),
    MUJER("Mujer"),
    UNISEX("Unisex"),
    NINO("Niño"),
    NINA("Niña");

    private final String displayValue;

    Gender(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}