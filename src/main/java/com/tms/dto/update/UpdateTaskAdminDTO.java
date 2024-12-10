package com.tms.dto.update;

import com.tms.enumeration.TaskPriority;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Getter;

import org.openapitools.jackson.nullable.JsonNullable;

@Getter
public class UpdateTaskAdminDTO extends UpdateTaskDTO {
    private JsonNullable<String> title;

    private JsonNullable<String> description;

    @Enumerated(EnumType.STRING)
    private JsonNullable<TaskPriority> taskPriority;

    private JsonNullable<Long> executionerID;

    private JsonNullable<String> additionalCommentName;

    private JsonNullable<String> additionalCommentText;
}
