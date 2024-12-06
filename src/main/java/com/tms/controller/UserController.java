package com.tms.controller;

import com.tms.dto.UserDTO;
import com.tms.model.User;
import com.tms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tms.controller.UserController.USER_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + USER_CONTROLLER_PATH)
@AllArgsConstructor
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID_PATH = "/{id}";
    private final UserService userService;

    @Operation(summary = "Get user / Method for this user only")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = User.class))
    )
    @GetMapping(ID_PATH)
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUser(id));
    }

    @Operation(summary = "Get all users / Method for admin only")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = User.class)))
    )
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @Operation(summary = "Create new user / Method for admin only")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.created(null).body(userService.createUser(userDTO));
    }

    @Operation(summary = "Update user / Method for this user only")
    @ApiResponse(responseCode = "200", description = "User updated", content = @Content(
            schema = @Schema(implementation = User.class))
    )
    @PutMapping(ID_PATH)
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok().body(userService.updateUser(id, userDTO));
    }

    @Operation(summary = "Delete user / Method for this user only")
    @ApiResponse(responseCode = "200", description = "User deleted")
    @DeleteMapping(ID_PATH)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
