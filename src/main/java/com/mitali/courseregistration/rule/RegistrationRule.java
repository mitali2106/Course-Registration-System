package com.mitali.courseregistration.rule;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Student;

public interface RegistrationRule {

    void check(Student student, Course course);
}