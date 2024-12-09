package com.tms.dto.update;

import com.tms.enumeration.TaskPriority;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.openapitools.jackson.nullable.JsonNullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateTaskAdminDTO extends UpdateTaskDTO {
    private JsonNullable<String> title;

    private JsonNullable<String> description;

    @Enumerated(EnumType.STRING)
    private JsonNullable<TaskPriority> taskPriority;

    private JsonNullable<Long> executionerID;
}
