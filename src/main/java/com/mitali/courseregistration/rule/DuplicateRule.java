package com.mitali.courseregistration.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Student;
import com.mitali.courseregistration.repository.RegistrationRepository;

@Component
@Order(1)
public class DuplicateRule implements RegistrationRule {

    private final RegistrationRepository registrations;

    public DuplicateRule(RegistrationRepository registrations) {
        this.registrations = registrations;
    }

    @Override
    public void check(Student student, Course course) {
        if (registrations.existsByStudentAndCourse(student, course)) {
            throw new RuleViolationException("DUPLICATE",
                    "Already registered for " + course.getCode());
        }
    }
}