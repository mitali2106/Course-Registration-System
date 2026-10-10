package com.mitali.courseregistration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String regNo;

    private int maxCredits;

    protected Student() {
    }

    public Student(String name, String regNo, int maxCredits) {
        this.name = name;
        this.regNo = regNo;
        this.maxCredits = maxCredits;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegNo() {
        return regNo;
    }

    public int getMaxCredits() {
        return maxCredits;
    }
}