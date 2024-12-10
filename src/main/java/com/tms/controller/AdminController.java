package com.tms.controller;

import com.tms.dto.UserDTO;
import com.tms.model.user.Admin;
import com.tms.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.tms.controller.AdminController.ADMIN_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + ADMIN_CONTROLLER_PATH)
@AllArgsConstructor
public class AdminController {
    public static final String ADMIN_CONTROLLER_PATH = "/admin";
    public static final String ONLY_ADMIN = "hasAuthority('ROLE_ADMIN')";
    private final AdminService adminService;

    @Operation(summary = "Get admin / Method for admin only")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = Admin.class))
    )
    @GetMapping
    @PreAuthorize(ONLY_ADMIN)
    public ResponseEntity<Admin> getAdmin() {
        return ResponseEntity.ok().body(adminService.getAdmin());
    }

    @Operation(summary = "Update admin / Method for admin only")
    @ApiResponse(responseCode = "200", description = "Admin updated", content = @Content(
            schema = @Schema(implementation = Admin.class))
    )
    @PutMapping
    @PreAuthorize(ONLY_ADMIN)
    public ResponseEntity<Admin> updateAdmin(@RequestBody @Valid UserDTO adminDTO) {
        return ResponseEntity.ok().body(adminService.updateAdmin(adminDTO));
    }
}
