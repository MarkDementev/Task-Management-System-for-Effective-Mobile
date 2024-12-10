package com.tms.controller;

import com.tms.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tms.controller.CommentController.COMMENT_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + COMMENT_CONTROLLER_PATH)
@AllArgsConstructor
public class CommentController {
    public static final String COMMENT_CONTROLLER_PATH = "/comments";
    public static final String ID_PATH = "/{id}";
    private final CommentService commentService;

    @Operation(summary = "Delete comment / Method for users and admin")
    @ApiResponse(responseCode = "200", description = "Comment deleted")
    @DeleteMapping(ID_PATH)
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
