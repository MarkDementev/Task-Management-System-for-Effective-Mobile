package com.tms.dto.update;

import com.tms.enumeration.TaskStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Data;

import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class UpdateTaskDTO {
    @Enumerated(EnumType.STRING)
    private JsonNullable<TaskStatus> taskStatus;
}
