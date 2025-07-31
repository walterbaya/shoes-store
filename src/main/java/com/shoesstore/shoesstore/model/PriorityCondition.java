package com.shoesstore.shoesstore.model;

public enum PriorityCondition {
    NORMAL("Normal"),
    ALTA("Alta"),
    URGENTE("Urgente");

    private final String displayName;

    PriorityCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}