package com.tms.service;

import com.tms.dto.UserDTO;
import com.tms.model.User;

import java.util.List;

public interface UserService {
    User getUser(Long id);
    List<User> getUsers();
    User createUser(UserDTO userDTO);
    User updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}
