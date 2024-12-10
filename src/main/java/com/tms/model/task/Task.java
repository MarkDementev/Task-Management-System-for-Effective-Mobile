package com.tms.model.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.tms.enumeration.TaskPriority;
import com.tms.enumeration.TaskStatus;
import com.tms.model.user.User;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "tasks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String title;

    @NotBlank
    @Column(unique = true)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskPriority taskPriority;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "author_id")
    private User author;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "executioner_id")
    private User executioner;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Task(String title, String description, TaskPriority taskPriority) {
        this.title = title;
        this.description = description;
        this.taskStatus = TaskStatus.WAITING;
        this.taskPriority = taskPriority;
        this.comments = new ArrayList<>();
    }

    public Task(String title, String description, TaskPriority taskPriority, User executioner) {
        this.title = title;
        this.description = description;
        this.taskStatus = TaskStatus.WAITING;
        this.taskPriority = taskPriority;
        this.comments = new ArrayList<>();
        this.executioner = executioner;
    }
}
