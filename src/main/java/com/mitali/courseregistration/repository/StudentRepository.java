package com.mitali.courseregistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitali.courseregistration.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}