package com.tms.enumeration;

public enum TaskStatus {
    WAITING("Task in waiting!"),
    IN_PROCESS("Task in process..."),
    ENDED("Task is ended.");

    private final String enumValue;

    TaskStatus(String enumValue) {
        this.enumValue = enumValue;
    }
}
