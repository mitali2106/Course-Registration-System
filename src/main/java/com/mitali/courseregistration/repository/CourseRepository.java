package com.mitali.courseregistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitali.courseregistration.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}