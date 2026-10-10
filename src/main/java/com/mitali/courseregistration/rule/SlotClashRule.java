package com.mitali.courseregistration.rule;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Student;
import com.mitali.courseregistration.repository.Clash;
import com.mitali.courseregistration.repository.RegistrationRepository;

@Component
@Order(2)
public class SlotClashRule implements RegistrationRule {

    private final RegistrationRepository registrations;

    public SlotClashRule(RegistrationRepository registrations) {
        this.registrations = registrations;
    }

    @Override
    public void check(Student student, Course course) {
        List<Clash> clashes = registrations.findClashes(student, course.getSlot());
        if (!clashes.isEmpty()) {
            Clash first = clashes.get(0);
            throw new RuleViolationException("SLOT_CLASH",
                    course.getCode() + " clashes with " + first.courseCode()
                            + " on " + first.dayOfWeek() + " period " + first.periodNo());
        }
    }
}