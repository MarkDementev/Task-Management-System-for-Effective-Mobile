package com.tms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String text;

    @NotNull
    private Long authorID;

    @NotNull
    private Long taskID;
}
