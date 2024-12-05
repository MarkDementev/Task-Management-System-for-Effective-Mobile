package com.tms.service.impl;

import com.tms.dto.UserDTO;
import com.tms.model.Admin;
import com.tms.repository.AdminRepository;
import com.tms.service.AdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Override
    public Admin getAdmin() {
        return adminRepository.findAll().get(0);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public Admin updateAdmin(UserDTO adminDTO) {
        AtomicReference<Admin> atomicAdminToUpdate = new AtomicReference<>(
                adminRepository.findAll().get(0)
        );

        atomicAdminToUpdate.get().setEmail(adminDTO.getEmail());
        atomicAdminToUpdate.get().setPassword(adminDTO.getPassword());

        return adminRepository.save(atomicAdminToUpdate.get());
    }
}
