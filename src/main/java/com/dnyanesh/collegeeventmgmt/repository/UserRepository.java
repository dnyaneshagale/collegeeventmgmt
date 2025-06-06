package com.dnyanesh.collegeeventmgmt.repository;

import com.dnyanesh.collegeeventmgmt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
