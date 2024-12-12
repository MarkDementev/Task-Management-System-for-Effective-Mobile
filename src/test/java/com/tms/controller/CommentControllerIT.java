package com.tms.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import com.tms.TestUtils;
import com.tms.config.SpringConfigForTests;
import com.tms.dto.CommentDTO;
import com.tms.dto.update.UpdateCommentDTO;
import com.tms.model.task.Comment;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openapitools.jackson.nullable.JsonNullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.tms.TestUtils.*;
import static com.tms.config.SecurityConfig.ADMIN_NAME;
import static com.tms.config.SpringConfigForTests.TEST_PROFILE;
import static com.tms.controller.CommentController.COMMENT_CONTROLLER_PATH;
import static com.tms.controller.CommentController.COMMENT_UPDATE_PATH;
import static com.tms.controller.UserController.ID_PATH;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class CommentControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeTest() throws Exception {
        testUtils.addAdmin();
        testUtils.login(testUtils.getAdminLoginDto());
    }

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void createCommentIT() throws Exception {
        testUtils.createDefaultTask();

        CommentDTO commentDTO = new CommentDTO(
                new StringBuilder(DEFAULT_TASK_COMMENT_NAME).reverse().toString(),
                new StringBuilder(DEFAULT_TASK_COMMENT_TEXT).reverse().toString(),
                userRepository.findByEmail(DEFAULT_USER_EMAIL).get().getId(),
                taskRepository.findAll().get(0).getId()
        );
        var response = testUtils.perform(
                        post(testUtils.baseUrl + COMMENT_CONTROLLER_PATH)
                                .content(asJson(commentDTO))
                                .contentType(APPLICATION_JSON),
                        ADMIN_NAME
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        Comment commentFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(commentDTO.getName(), commentFromResponse.getName());
        assertEquals(commentDTO.getText(), commentFromResponse.getText());
        assertEquals(commentDTO.getAuthorID(), commentFromResponse.getAuthor().getId());
        assertEquals(commentFromResponse.getId(), taskRepository.findAll().get(0).getComments().get(1).getId());
    }

    @Test
    public void updateCommentIT() throws Exception {
        testUtils.createDefaultTask();

        UpdateCommentDTO commentDTO = new UpdateCommentDTO(
                JsonNullable.of(new StringBuilder(DEFAULT_TASK_COMMENT_TEXT).reverse().toString())
        );
        Long createdCommentId = taskRepository.findAll().get(0).getComments().get(0).getId();
        var response = testUtils.perform(
                        put(testUtils.baseUrl + COMMENT_CONTROLLER_PATH + COMMENT_UPDATE_PATH + ID_PATH,
                                createdCommentId)
                                .content(asJson(commentDTO))
                                .contentType(APPLICATION_JSON),
                        ADMIN_NAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Comment commentFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(commentDTO.getText().get(), commentFromResponse.getText());
    }

    @Test
    public void deleteCommentIT() throws Exception {
        testUtils.createDefaultTask();

        assertEquals(1, commentRepository.findAll().size());

        Long createdCommentId = taskRepository.findAll().get(0).getComments().get(0).getId();

        testUtils.perform(
                        delete(testUtils.baseUrl + COMMENT_CONTROLLER_PATH + ID_PATH, createdCommentId),
                        ADMIN_NAME)
                .andExpect(status().isOk());
        assertEquals(0, commentRepository.findAll().size());
    }
}
