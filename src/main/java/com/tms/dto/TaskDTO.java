package com.tms.dto;

import com.tms.enumeration.TaskPriority;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskPriority taskPriority;

    private Long executorID;

    private String initialCommentName;

    private String initialCommentText;
}
