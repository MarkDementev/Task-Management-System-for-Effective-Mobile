package com.tms.repository;

import com.tms.model.task.Task;
import com.tms.model.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<List<Task>> findByAuthor(User user, Pageable pageable);
    Page<List<Task>> findByExecutor(User user, Pageable pageable);
}
