package com.dnyanesh.collegeeventmgmt.repository;

import com.dnyanesh.collegeeventmgmt.model.EventRegistration;
import com.dnyanesh.collegeeventmgmt.model.Event;
import com.dnyanesh.collegeeventmgmt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEvent(Event event);
    List<EventRegistration> findByUser(User user);
    Optional<EventRegistration> findByEventAndUser(Event event, User user);
}