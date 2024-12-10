package com.tms.service;

import com.tms.dto.CommentDTO;
import com.tms.model.task.Comment;

public interface CommentService {
    Comment createComment(CommentDTO commentDTO);
    void deleteComment(Long id);
}
