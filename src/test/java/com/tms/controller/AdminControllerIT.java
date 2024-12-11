package com.tms.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import com.tms.TestUtils;
import com.tms.config.SpringConfigForTests;
import com.tms.model.user.Admin;
import com.tms.repository.AdminRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.tms.TestUtils.asJson;
import static com.tms.TestUtils.fromJson;
import static com.tms.config.SecurityConfig.ADMIN_NAME;
import static com.tms.config.SecurityConfig.LOGIN_PATH;
import static com.tms.config.SpringConfigForTests.TEST_PROFILE;
import static com.tms.controller.AdminController.ADMIN_CONTROLLER_PATH;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class AdminControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    public void loginBeforeTest() throws Exception {
        testUtils.login(testUtils.getAdminLoginDto());
    }

    @Test
    public void getAdminIT() throws Exception {
        Admin adminCreatedAtStart = adminRepository.findByIsAdmin(true);
        var response = testUtils.perform(
                        get("/tms" + ADMIN_CONTROLLER_PATH),
                        ADMIN_NAME
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Admin adminFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(adminCreatedAtStart.getId(), adminFromResponse.getId());
        assertEquals(adminCreatedAtStart.getEmail(), adminFromResponse.getEmail());
        assertEquals(adminFromResponse.getIsAdmin(), true);
    }

    @Test
    public void updateAdminIT() throws Exception {
        Admin adminCreatedAtStart = adminRepository.findByIsAdmin(true);
        var response = testUtils.perform(
                        put("/tms" + ADMIN_CONTROLLER_PATH)
                        .content(asJson(testUtils.getUpdateAdminDTO())).contentType(APPLICATION_JSON),
                        ADMIN_NAME
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Admin adminFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(adminCreatedAtStart.getId(), adminFromResponse.getId());
        assertEquals(testUtils.getUpdateAdminDTO().getEmail(), adminFromResponse.getEmail());
        assertNotNull(adminFromResponse.getUpdatedAt());
        testUtils.perform(post(LOGIN_PATH).content(asJson(testUtils.getUpdateAdminDTO())).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
