package com.tms.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import com.tms.TestUtils;
import com.tms.config.SpringConfigForTests;
import com.tms.model.user.User;
import com.tms.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.tms.TestUtils.asJson;
import static com.tms.TestUtils.fromJson;
import static com.tms.config.SecurityConfig.ADMIN_NAME;
import static com.tms.config.SecurityConfig.LOGIN_PATH;
import static com.tms.config.SpringConfigForTests.TEST_PROFILE;
import static com.tms.controller.UserController.ID_PATH;
import static com.tms.controller.UserController.USER_CONTROLLER_PATH;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class UserControllerIT {
    @Autowired
    private TestUtils testUtils;
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
    public void getUserIT() throws Exception {
        testUtils.createDefaultUser();

        User expectedUser = userRepository.findByEmail(testUtils.getDefaultUserDTO().getEmail()).get();
        var response = testUtils.perform(
                get(testUtils.baseUrl + USER_CONTROLLER_PATH + ID_PATH, expectedUser.getId()),
                        testUtils.getDefaultUserDTO().getEmail()
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        User userFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), userFromResponse.getId());
        assertEquals(expectedUser.getEmail(), userFromResponse.getEmail());
        assertEquals(expectedUser.getIsAdmin(), userFromResponse.getIsAdmin());
        assertNotNull(userFromResponse.getCreatedAt());
        assertNotNull(userFromResponse.getUpdatedAt());
        testUtils.perform(post(LOGIN_PATH).content(asJson(testUtils.getDefaultUserDTO())).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUsersIT() throws Exception {
        testUtils.createDefaultUser();

        var response = testUtils.perform(
                get(testUtils.baseUrl + USER_CONTROLLER_PATH),
                        ADMIN_NAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<User> allUsers = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(allUsers).hasSize(2);
    }

    @Test
    public void createUserIT() throws Exception {
        var response = testUtils.perform(
                post(testUtils.baseUrl + USER_CONTROLLER_PATH)
                        .content(asJson(testUtils.getDefaultUserDTO()))
                        .contentType(APPLICATION_JSON),
                        ADMIN_NAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        User userFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertNotNull(userFromResponse.getId());
        assertEquals(testUtils.getDefaultUserDTO().getEmail(), userFromResponse.getEmail());
        assertEquals(false, userFromResponse.getIsAdmin());
        assertNotNull(userFromResponse.getCreatedAt());
        assertNotNull(userFromResponse.getUpdatedAt());
        testUtils.perform(post(LOGIN_PATH).content(asJson(testUtils.getDefaultUserDTO())).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserIT() throws Exception {
        testUtils.createDefaultUser();

        Long createdUserId = userRepository.findByEmail(testUtils.getDefaultUserDTO().getEmail()).get().getId();
        var response = testUtils.perform(
                put(testUtils.baseUrl + USER_CONTROLLER_PATH + ID_PATH, createdUserId)
                        .content(asJson(testUtils.getUpdateUserDTO())).contentType(APPLICATION_JSON),
                        testUtils.getDefaultUserDTO().getEmail()
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        User userFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(createdUserId, userFromResponse.getId());
        assertEquals(testUtils.getUpdateUserDTO().getEmail(), userFromResponse.getEmail());
        assertEquals(false, userFromResponse.getIsAdmin());
        assertNotNull(userFromResponse.getCreatedAt());
        assertNotNull(userFromResponse.getUpdatedAt());
        testUtils.perform(post(LOGIN_PATH).content(asJson(testUtils.getUpdateUserDTO())).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserIT() throws Exception {
        testUtils.createDefaultUser();

        Long createdUserId = userRepository.findByEmail(testUtils.getDefaultUserDTO().getEmail()).get().getId();

        testUtils.perform(
                delete(testUtils.baseUrl + USER_CONTROLLER_PATH + ID_PATH, createdUserId),
                        testUtils.getDefaultUserDTO().getEmail())
                .andExpect(status().isOk());
        assertEquals(Optional.empty(), userRepository.findByEmail(testUtils.getDefaultUserDTO().getEmail()));
    }
}
