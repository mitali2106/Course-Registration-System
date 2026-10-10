package com.mitali.courseregistration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Registration;
import com.mitali.courseregistration.model.Student;
import com.mitali.courseregistration.repository.CourseRepository;
import com.mitali.courseregistration.repository.RegistrationRepository;
import com.mitali.courseregistration.repository.SlotRepository;
import com.mitali.courseregistration.repository.StudentRepository;
import com.mitali.courseregistration.rule.RuleViolationException;
import com.mitali.courseregistration.service.RegistrationService;

@SpringBootTest
@Transactional
class RegistrationServiceTest {

    @Autowired
    private RegistrationService service;

    @Autowired
    private StudentRepository students;

    @Autowired
    private CourseRepository courses;

    @Autowired
    private SlotRepository slots;

    @Autowired
    private RegistrationRepository registrations;

    @Test
    void slotClash_allowsNonOverlappingSlots() {
        Student student = student(20);
        service.register(student.getId(), course("T101", 4, 30, "A1").getId());
        Course other = course("T102", 4, 30, "B1");
        assertDoesNotThrow(() -> service.register(student.getId(), other.getId()));
    }

    @Test
    void slotClash_rejectsOverlappingSlots() {
        Student student = student(20);
        service.register(student.getId(), course("T101", 4, 30, "A1").getId());
        Course lab = course("T102", 2, 30, "L1");
        assertRejectedBy("SLOT_CLASH", () -> service.register(student.getId(), lab.getId()));
    }

    @Test
    void seatCap_allowsWhileSeatsRemain() {
        Course course = course("T101", 4, 2, "A1");
        service.register(student(20).getId(), course.getId());
        Student second = student(20);
        assertDoesNotThrow(() -> service.register(second.getId(), course.getId()));
    }

    @Test
    void seatCap_rejectsWhenFull() {
        Course course = course("T101", 4, 1, "A1");
        service.register(student(20).getId(), course.getId());
        Student second = student(20);
        assertRejectedBy("SEAT_CAP", () -> service.register(second.getId(), course.getId()));
    }

    @Test
    void creditLimit_allowsExactlyTheLimit() {
        Student student = student(8);
        service.register(student.getId(), course("T101", 4, 30, "A1").getId());
        Course second = course("T102", 4, 30, "B1");
        assertDoesNotThrow(() -> service.register(student.getId(), second.getId()));
    }

    @Test
    void creditLimit_rejectsAboveLimit() {
        Student student = student(8);
        service.register(student.getId(), course("T101", 4, 30, "A1").getId());
        service.register(student.getId(), course("T102", 4, 30, "B1").getId());
        Course third = course("T103", 4, 30, "C1");
        assertRejectedBy("CREDIT_LIMIT", () -> service.register(student.getId(), third.getId()));
    }

    @Test
    void duplicate_allowsSameCourseForDifferentStudents() {
        Course course = course("T101", 4, 30, "A1");
        service.register(student(20).getId(), course.getId());
        Student second = student(20);
        assertDoesNotThrow(() -> service.register(second.getId(), course.getId()));
    }

    @Test
    void duplicate_rejectsSameCourseTwice() {
        Student student = student(20);
        Course course = course("T101", 4, 30, "A1");
        service.register(student.getId(), course.getId());
        assertRejectedBy("DUPLICATE", () -> service.register(student.getId(), course.getId()));
    }

    @Test
    void register_savesRegistration() {
        Student student = student(20);
        Course course = course("T101", 4, 30, "A1");
        service.register(student.getId(), course.getId());
        assertEquals(1, registrations.countByCourse(course));
        assertEquals(1, service.findByStudent(student.getId()).size());
    }

    @Test
    void drop_freesTheSeat() {
        Course course = course("T101", 4, 1, "A1");
        Registration first = service.register(student(20).getId(), course.getId());
        service.drop(first.getId());
        Student second = student(20);
        assertDoesNotThrow(() -> service.register(second.getId(), course.getId()));
        assertEquals(1, registrations.countByCourse(course));
    }

    private Student student(int maxCredits) {
        return students.save(new Student("Test", "REG" + System.nanoTime(), maxCredits));
    }

    private Course course(String code, int credits, int capacity, String slotCode) {
        return courses.save(new Course(code, "Title " + code, credits, capacity,
                slots.findByCode(slotCode).orElseThrow()));
    }

    private void assertRejectedBy(String rule, Executable action) {
        RuleViolationException ex = assertThrows(RuleViolationException.class, action);
        assertEquals(rule, ex.getRule());
    }
}