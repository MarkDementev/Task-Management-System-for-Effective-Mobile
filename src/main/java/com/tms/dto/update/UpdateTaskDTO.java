package com.tms.dto.update;

import com.tms.enumeration.TaskStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.openapitools.jackson.nullable.JsonNullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskDTO {
    @Enumerated(EnumType.STRING)
    private JsonNullable<TaskStatus> taskStatus;

    //TODO дай возможность добавить коммент (это может быть нужно и при создании, и при апдейте)
}
