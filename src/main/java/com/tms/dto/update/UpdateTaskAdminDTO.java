package com.tms.dto.update;

import com.tms.enumeration.TaskPriority;
import com.tms.enumeration.TaskStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskAdminDTO extends UpdateTaskDTO {
    private JsonNullable<String> title;

    private JsonNullable<String> description;

    @Enumerated(EnumType.STRING)
    private JsonNullable<TaskPriority> taskPriority;

    private JsonNullable<Long> executorID;

    private JsonNullable<String> additionalCommentName;

    private JsonNullable<String> additionalCommentText;

    public UpdateTaskAdminDTO(JsonNullable<TaskStatus> taskStatus, JsonNullable<String> description,
                              JsonNullable<TaskPriority> taskPriority, JsonNullable<Long> executorID) {
        super(taskStatus);
        this.description = description;
        this.taskPriority = taskPriority;
        this.executorID = executorID;
    }
}
