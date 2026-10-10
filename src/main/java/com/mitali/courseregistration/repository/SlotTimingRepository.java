package com.mitali.courseregistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitali.courseregistration.model.Slot;
import com.mitali.courseregistration.model.SlotTiming;

public interface SlotTimingRepository extends JpaRepository<SlotTiming, Long> {

    List<SlotTiming> findBySlot(Slot slot);
}