package com.dnyanesh.collegeeventmgmt.service;

import com.dnyanesh.collegeeventmgmt.dto.UserDto;
import com.dnyanesh.collegeeventmgmt.model.User;
import com.dnyanesh.collegeeventmgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(user);
    }

    public UserDto updateCurrentUser(String email, UserDto userDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(userDto.getFullName());
        // Do NOT update email here!
        return toDto(userRepository.save(user));
    }

    public UserDto updateEmail(String currentEmail, String newEmail, String password) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: Check password for confirmation
        if (password != null && !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password incorrect");
        }

        // Check for duplicate email
        if (userRepository.findByEmail(newEmail).isPresent() && !currentEmail.equalsIgnoreCase(newEmail)) {
            throw new RuntimeException("Email already in use");
        }

        user.setEmail(newEmail);
        return toDto(userRepository.save(user));
    }

    public void updatePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}