package com.tms.exception;

public class EntityWithIDNotFoundException extends NullPointerException {
    public static final String MESSAGE = " entity with this id doesn't exist! Wrong id is ";

    public EntityWithIDNotFoundException(String entityName, Long id) {
        super(entityName + MESSAGE + id);
    }
}