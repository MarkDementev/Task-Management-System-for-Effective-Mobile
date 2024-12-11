package com.tms.dto.update;

import lombok.Data;

import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class UpdateCommentDTO {
    private JsonNullable<String> name;

    private JsonNullable<String> text;
}
