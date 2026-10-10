package com.mitali.courseregistration.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Student;
import com.mitali.courseregistration.repository.RegistrationRepository;

@Component
@Order(3)
public class CreditLimitRule implements RegistrationRule {

    private final RegistrationRepository registrations;

    public CreditLimitRule(RegistrationRepository registrations) {
        this.registrations = registrations;
    }

    @Override
    public void check(Student student, Course course) {
        long used = registrations.sumCreditsByStudent(student);
        long afterAdding = used + course.getCredits();
        if (afterAdding > student.getMaxCredits()) {
            throw new RuleViolationException("CREDIT_LIMIT",
                    "Adding " + course.getCode() + " needs " + afterAdding
                            + " credits, limit is " + student.getMaxCredits());
        }
    }
}