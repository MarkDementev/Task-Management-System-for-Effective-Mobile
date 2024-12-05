package com.tms.controller;

import com.tms.dto.UserDTO;
import com.tms.model.Admin;
import com.tms.service.AdminService;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.tms.controller.AdminController.ADMIN_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + ADMIN_CONTROLLER_PATH)
@AllArgsConstructor
public class AdminController {
    public static final String ADMIN_CONTROLLER_PATH = "/admin";
    private final AdminService adminService;

    //TODO добавь опенапи док и секюрити для админа онли
    @GetMapping
    public ResponseEntity<Admin> getAdmin() {
        return ResponseEntity.ok().body(adminService.getAdmin());
    }

    //TODO добавь опенапи док и секюрити для админа онли
    @PutMapping
    public ResponseEntity<Admin> updateAdmin(@RequestBody @Valid UserDTO adminDTO) {
        return ResponseEntity.ok().body(adminService.updateAdmin(adminDTO));
    }
}
