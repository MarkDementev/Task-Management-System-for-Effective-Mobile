package com.tms.exception;

public class AdminDeletionForbiddenException extends UnsupportedOperationException {
    public static final String MESSAGE = "Admin deletion is forbidden in this application!";

    public AdminDeletionForbiddenException() {
        super(MESSAGE);
    }
}
