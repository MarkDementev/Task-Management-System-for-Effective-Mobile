package com.tms.controller;

import com.tms.dto.CommentDTO;
import com.tms.dto.update.UpdateCommentDTO;
import com.tms.model.task.Comment;
import com.tms.repository.CommentRepository;
import com.tms.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.tms.controller.CommentController.COMMENT_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + COMMENT_CONTROLLER_PATH)
@RequiredArgsConstructor
public class CommentController {
    public static final String COMMENT_CONTROLLER_PATH = "/comments";
    public static final String COMMENT_UPDATE_PATH = "/update";
    public static final String ID_PATH = "/{id}";
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    @Operation(summary = "Create new comment / Method for authenticated admin and users")
    @ApiResponse(responseCode = "201", description = "Comment created")
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody @Valid CommentDTO commentDTO) {
        return ResponseEntity.created(null).body(commentService.createComment(commentDTO));
    }

    @Operation(summary = "Update comment / Method for user - comment author only")
    @ApiResponse(responseCode = "200", description = "Comment updated", content = @Content(
            schema = @Schema(implementation = Comment.class))
    )
    @PutMapping(COMMENT_UPDATE_PATH + ID_PATH)
    @PreAuthorize("@commentRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id,
                                                 @RequestBody @Valid UpdateCommentDTO updateCommentDTO) {
        return ResponseEntity.ok().body(commentService.updateComment(id, updateCommentDTO));
    }

    @Operation(summary = "Delete comment / Method for users-comment authors and admin")
    @ApiResponse(responseCode = "200", description = "Comment deleted")
    @DeleteMapping(ID_PATH)
    @PreAuthorize("@commentRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
