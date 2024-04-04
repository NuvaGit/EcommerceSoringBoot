package com.libary.service;


import com.libary.dto.AdminDto;
import com.libary.model.Admin;

public interface AdminService {
    Admin findByUsername(String username);

    Admin save(AdminDto adminDto);
}