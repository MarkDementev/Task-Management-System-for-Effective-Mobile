package com.tms.enumeration;

public enum TaskPriority {
    HIGH("High priority task!!!"),
    AVERAGE("Average priority task!"),
    LOW("Low priority task.");

    private final String enumValue;

    TaskPriority(String enumValue) {
        this.enumValue = enumValue;
    }
}
