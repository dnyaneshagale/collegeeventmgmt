package com.dnyanesh.collegeeventmgmt.dto;

import com.dnyanesh.collegeeventmgmt.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private Role role;
}