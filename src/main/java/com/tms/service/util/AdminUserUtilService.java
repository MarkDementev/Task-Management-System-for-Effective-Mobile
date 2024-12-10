package com.tms.service.util;

import com.tms.dto.UserDTO;
import com.tms.model.user.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class AdminUserUtilService {
    public static void getFromDTOThenSetAll(AtomicReference<? extends User> entityToWorkWith, UserDTO userDTO,
                                            PasswordEncoder passwordEncoder) {
        String passwordEncoded = passwordEncoder.encode(userDTO.getPassword());

        entityToWorkWith.get().setEmail(userDTO.getEmail());
        entityToWorkWith.get().setPassword(passwordEncoded);
    }
}
