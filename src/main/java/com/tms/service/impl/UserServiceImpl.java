package com.tms.service.impl;

import com.tms.dto.UserDTO;
import com.tms.exception.EntityWithIDNotFoundException;
import com.tms.exception.AdminDeletionForbiddenException;
import com.tms.model.user.Admin;
import com.tms.model.user.User;
import com.tms.repository.UserRepository;
import com.tms.service.UserService;
import com.tms.service.util.AdminUserUtilService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                User.class.getSimpleName(), id));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public User createUser(UserDTO userDTO) {
        String passwordEncoded = passwordEncoder.encode(userDTO.getPassword());
        AtomicReference<User> atomicNewUser = new AtomicReference<>(new User(userDTO.getEmail(),
                passwordEncoded));

        return userRepository.save(atomicNewUser.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public User updateUser(Long id, UserDTO userDTO) {
        AtomicReference<User> atomicUserToUpdate = new AtomicReference<>(
                userRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        User.class.getSimpleName(), id))
        );

        AdminUserUtilService.getFromDTOThenSetAll(atomicUserToUpdate, userDTO, passwordEncoder);
        return userRepository.save(atomicUserToUpdate.get());
    }

    @Override
    public void deleteUser(Long id) {
        User userToDelete = userRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                User.class.getSimpleName() + " or " + Admin.class.getSimpleName(), id));

        if (userToDelete.getIsAdmin()) {
            throw new AdminDeletionForbiddenException();
        } else {
            userRepository.deleteById(id);
        }
    }
}
