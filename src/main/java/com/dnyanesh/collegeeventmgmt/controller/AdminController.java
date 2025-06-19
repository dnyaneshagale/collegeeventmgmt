package com.dnyanesh.collegeeventmgmt.controller;

import com.dnyanesh.collegeeventmgmt.dto.UserDto;
import com.dnyanesh.collegeeventmgmt.exception.ResourceNotFoundException;
import com.dnyanesh.collegeeventmgmt.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Admin";
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable UUID id) {
        UserDto user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    @PutMapping("/users/{id}")
    public UserDto updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUserById(id, userDto);
        if (updatedUser == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return updatedUser;
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUserById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return ResponseEntity.ok().build();
    }
}