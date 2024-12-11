package com.tms.service;

import com.tms.dto.CommentDTO;
import com.tms.dto.update.UpdateCommentDTO;
import com.tms.model.task.Comment;

public interface CommentService {
    Comment createComment(CommentDTO commentDTO);
    Comment updateComment(Long id, UpdateCommentDTO updateCommentDTO);
    void deleteComment(Long id);
}
