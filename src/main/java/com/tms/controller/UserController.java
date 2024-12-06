package com.tms.controller;

import com.tms.dto.UserDTO;
import com.tms.model.User;
import com.tms.service.UserService;

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

    //TODO добавь опенапи док
    //TODO add секюрити для юзера онли
    @GetMapping(ID_PATH)
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUser(id));
    }

    //TODO добавь опенапи док
    //TODO add секюрити для админа онли
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    //TODO добавь опенапи док
    //TODO add секюрити для админа онли
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.created(null).body(userService.createUser(userDTO));
    }

    //TODO добавь опенапи док
    //TODO add секюрити для юзера онли
    @PutMapping(ID_PATH)
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok().body(userService.updateUser(id, userDTO));
    }

    //TODO добавь опенапи док
    //TODO add секюрити для юзера онли
    @DeleteMapping(ID_PATH)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
