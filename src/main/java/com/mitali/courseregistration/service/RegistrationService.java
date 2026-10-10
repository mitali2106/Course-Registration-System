package com.mitali.courseregistration.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Registration;
import com.mitali.courseregistration.model.Student;
import com.mitali.courseregistration.repository.CourseRepository;
import com.mitali.courseregistration.repository.RegistrationRepository;
import com.mitali.courseregistration.repository.StudentRepository;
import com.mitali.courseregistration.rule.RegistrationRule;

@Service
public class RegistrationService {

    private final StudentRepository students;
    private final CourseRepository courses;
    private final RegistrationRepository registrations;
    private final List<RegistrationRule> rules;

    public RegistrationService(StudentRepository students, CourseRepository courses,
            RegistrationRepository registrations, List<RegistrationRule> rules) {
        this.students = students;
        this.courses = courses;
        this.registrations = registrations;
        this.rules = rules;
    }

    @Transactional
    public Registration register(Long studentId, Long courseId) {
        Student student = students.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student " + studentId + " not found"));
        Course course = courses.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course " + courseId + " not found"));

        for (RegistrationRule rule : rules) {
            rule.check(student, course);
        }

        return registrations.save(new Registration(student, course));
    }

    @Transactional
    public void drop(Long registrationId) {
        if (!registrations.existsById(registrationId)) {
            throw new NotFoundException("Registration " + registrationId + " not found");
        }
        registrations.deleteById(registrationId);
    }

    @Transactional(readOnly = true)
    public List<Registration> findByStudent(Long studentId) {
        Student student = students.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student " + studentId + " not found"));
        return registrations.findByStudent(student);
    }
}