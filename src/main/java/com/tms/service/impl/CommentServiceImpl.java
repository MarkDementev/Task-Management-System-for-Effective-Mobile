package com.tms.service.impl;

import com.tms.dto.CommentDTO;
import com.tms.dto.update.UpdateCommentDTO;
import com.tms.exception.EntityWithIDNotFoundException;
import com.tms.model.task.Comment;
import com.tms.model.task.Task;
import com.tms.model.user.User;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;
import com.tms.service.CommentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    @Override
    public Comment createComment(CommentDTO commentDTO) {
        Long userID = commentDTO.getAuthorID();
        Long taskID = commentDTO.getTaskID();
        User user = userRepository.findById(userID).orElseThrow(() -> new EntityWithIDNotFoundException(
                User.class.getSimpleName(), userID));
        Task task = taskRepository.findById(taskID).orElseThrow(() -> new EntityWithIDNotFoundException(
                Task.class.getSimpleName(), taskID));
        AtomicReference<Comment> atomicNewComment = new AtomicReference<>(new Comment(commentDTO.getName(),
                commentDTO.getText(), user, task));

        return commentRepository.save(atomicNewComment.get());
    }

    @Override
    public Comment updateComment(Long id, UpdateCommentDTO updateCommentDTO) {
        AtomicReference<Comment> atomicCommentToUpdate = new AtomicReference<>(
                commentRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        Comment.class.getSimpleName(), id))
        );

        if (updateCommentDTO.getName() != null) {
            atomicCommentToUpdate.get().setName(updateCommentDTO.getName().get());
        }
        if (updateCommentDTO.getText() != null) {
            atomicCommentToUpdate.get().setText(updateCommentDTO.getText().get());
        }
        return commentRepository.save(atomicCommentToUpdate.get());
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
