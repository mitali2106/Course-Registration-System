package com.mitali.courseregistration.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Student;
import com.mitali.courseregistration.repository.RegistrationRepository;

@Component
@Order(4)
public class SeatCapRule implements RegistrationRule {

    private final RegistrationRepository registrations;

    public SeatCapRule(RegistrationRepository registrations) {
        this.registrations = registrations;
    }

    @Override
    public void check(Student student, Course course) {
        long taken = registrations.countByCourse(course);
        if (taken >= course.getCapacity()) {
            throw new RuleViolationException("SEAT_CAP",
                    course.getCode() + " is full (" + course.getCapacity() + " seats)");
        }
    }
}