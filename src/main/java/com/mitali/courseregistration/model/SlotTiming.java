package com.mitali.courseregistration.model;

import java.time.DayOfWeek;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SlotTiming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private int periodNo;

    protected SlotTiming() {
    }

    public SlotTiming(Slot slot, DayOfWeek dayOfWeek, int periodNo) {
        this.slot = slot;
        this.dayOfWeek = dayOfWeek;
        this.periodNo = periodNo;
    }

    public Long getId() {
        return id;
    }

    public Slot getSlot() {
        return slot;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public int getPeriodNo() {
        return periodNo;
    }
}