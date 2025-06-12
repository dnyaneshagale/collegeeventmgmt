package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.UserDto;
import com.dnyanesh.collegeeventmgmt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get current user profile
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails.getUsername()));
    }

    // Update current user profile (fullName only)
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateCurrentUser(userDetails.getUsername(), userDto));
    }

    // Update email (with password confirmation)
    @PutMapping("/me/email")
    public ResponseEntity<UserDto> updateEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        String newEmail = request.get("email");
        String password = request.get("password"); // Optional, but recommended
        return ResponseEntity.ok(userService.updateEmail(userDetails.getUsername(), newEmail, password));
    }

    // Update password
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        userService.updatePassword(userDetails.getUsername(), currentPassword, newPassword);
        return ResponseEntity.ok().body(Map.of("message", "Password updated successfully"));
    }
}