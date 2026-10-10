package com.mitali.courseregistration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitali.courseregistration.model.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    Optional<Slot> findByCode(String code);
}