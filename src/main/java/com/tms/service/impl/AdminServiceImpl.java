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
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public Admin updateAdmin(UserDTO adminDTO) {
        AtomicReference<Admin> atomicAdminToUpdate = new AtomicReference<>(
                adminRepository.findAll().get(0)
        );

        AdminUserUtilService.getFromDTOThenSetAll(atomicAdminToUpdate, adminDTO);
        return adminRepository.save(atomicAdminToUpdate.get());
    }
}
