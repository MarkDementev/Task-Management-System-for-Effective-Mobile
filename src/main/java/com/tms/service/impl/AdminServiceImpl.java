package com.tms.service.impl;

import com.tms.dto.UserDTO;
import com.tms.model.user.Admin;
import com.tms.repository.AdminRepository;
import com.tms.service.AdminService;
import com.tms.service.util.AdminUserUtilService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Override
    public Admin getAdmin() {
        return getAdminWithCheck();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public Admin updateAdmin(UserDTO adminDTO) {
        AtomicReference<Admin> atomicAdminToUpdate = new AtomicReference<>(getAdminWithCheck());

        AdminUserUtilService.getFromDTOThenSetAll(atomicAdminToUpdate, adminDTO);
        return adminRepository.save(atomicAdminToUpdate.get());
    }

    public Admin getAdminWithCheck() {
        Optional<Admin> adminOptional = Optional.ofNullable(adminRepository.findByIsAdmin(true));

        if (adminOptional.isEmpty()) {
            throw new RuntimeException("Admin always exists in this application, but for some reason is not found" +
                    " now!");
        } else {
            return adminOptional.get();
        }
    }
}
