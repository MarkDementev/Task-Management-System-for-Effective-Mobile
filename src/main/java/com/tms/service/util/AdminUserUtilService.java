package com.tms.service.util;

import com.tms.dto.UserDTO;
import com.tms.model.User;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class AdminUserUtilService {
    public static void getFromDTOThenSetAll(AtomicReference<? extends User> entityToWorkWith, UserDTO userDTO) {
        entityToWorkWith.get().setEmail(userDTO.getEmail());
        entityToWorkWith.get().setPassword(userDTO.getPassword());
    }
}
