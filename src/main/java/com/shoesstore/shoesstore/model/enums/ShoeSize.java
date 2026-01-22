package com.shoesstore.shoesstore.model.enums;

public enum ShoeSize {
    S35("35"), S36("36"), S37("37"), S38("38"),
    S39("39"), S40("40"), S41("41"), S42("42"),
    S43("43"), S44("44"), S45("45"), S46("46");

    private final String displayValue;

    ShoeSize(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}