package com.mitali.courseregistration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String title;

    private int credits;

    private int capacity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "slot_id")
    private Slot slot;

    protected Course() {
    }

    public Course(String code, String title, int credits, int capacity, Slot slot) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.capacity = capacity;
        this.slot = slot;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public int getCapacity() {
        return capacity;
    }

    public Slot getSlot() {
        return slot;
    }
}