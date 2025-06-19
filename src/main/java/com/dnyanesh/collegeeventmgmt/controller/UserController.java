package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.UserDto;
import com.dnyanesh.collegeeventmgmt.exception.ResourceNotFoundException;
import com.dnyanesh.collegeeventmgmt.service.UserService;
import jakarta.validation.Valid;
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

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser == null) {
            throw new ResourceNotFoundException("Current user not found");
        }
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateCurrentUser(userDetails.getUsername(), userDto);
        if (updatedUser == null) {
            throw new ResourceNotFoundException("Current user not found");
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/me/email")
    public ResponseEntity<UserDto> updateEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        String newEmail = request.get("email");
        String password = request.get("password");
        UserDto updatedEmailUser = userService.updateEmail(userDetails.getUsername(), newEmail, password);
        if (updatedEmailUser == null) {
            throw new ResourceNotFoundException("Current user not found");
        }
        return ResponseEntity.ok(updatedEmailUser);
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        boolean updated = userService.updatePassword(userDetails.getUsername(), currentPassword, newPassword);
        if (!updated) {
            throw new ResourceNotFoundException("Current user not found");
        }
        return ResponseEntity.ok().body(Map.of("message", "Password updated successfully"));
    }
}