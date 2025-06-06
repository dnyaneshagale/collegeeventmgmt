package com.dnyanesh.collegeeventmgmt.repository;

import com.dnyanesh.collegeeventmgmt.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}