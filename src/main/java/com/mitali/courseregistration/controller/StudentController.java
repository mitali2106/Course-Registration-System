package com.mitali.courseregistration.controller;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Registration;
import com.mitali.courseregistration.model.SlotTiming;
import com.mitali.courseregistration.model.Student;
import com.mitali.courseregistration.repository.RegistrationRepository;
import com.mitali.courseregistration.repository.SlotTimingRepository;
import com.mitali.courseregistration.repository.StudentRepository;
import com.mitali.courseregistration.service.NotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/students")
public class StudentController {

    public record CreateStudentRequest(@NotBlank String name, @NotBlank String regNo, @Min(1) int maxCredits) {
    }

    public record TimetableEntry(DayOfWeek day, int period, String courseCode, String title) {
    }

    public record CreditSummary(long used, int max, long remaining) {
    }

    private final StudentRepository students;
    private final RegistrationRepository registrations;
    private final SlotTimingRepository timings;

    public StudentController(StudentRepository students, RegistrationRepository registrations,
            SlotTimingRepository timings) {
        this.students = students;
        this.registrations = registrations;
        this.timings = timings;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody CreateStudentRequest request) {
        return students.save(new Student(request.name(), request.regNo(), request.maxCredits()));
    }

    @GetMapping
    public List<Student> list() {
        return students.findAll();
    }

    @GetMapping("/{id}/registrations")
    public List<Registration> registrations(@PathVariable Long id) {
        return registrations.findByStudent(find(id));
    }

    @GetMapping("/{id}/timetable")
    public List<TimetableEntry> timetable(@PathVariable Long id) {
        List<TimetableEntry> entries = new ArrayList<>();
        for (Registration registration : registrations.findByStudent(find(id))) {
            Course course = registration.getCourse();
            for (SlotTiming timing : timings.findBySlot(course.getSlot())) {
                entries.add(new TimetableEntry(timing.getDayOfWeek(), timing.getPeriodNo(),
                        course.getCode(), course.getTitle()));
            }
        }
        entries.sort(Comparator.comparing(TimetableEntry::day).thenComparingInt(TimetableEntry::period));
        return entries;
    }

    @GetMapping("/{id}/credits")
    public CreditSummary credits(@PathVariable Long id) {
        Student student = find(id);
        long used = registrations.sumCreditsByStudent(student);
        return new CreditSummary(used, student.getMaxCredits(), student.getMaxCredits() - used);
    }

    private Student find(Long id) {
        return students.findById(id)
                .orElseThrow(() -> new NotFoundException("Student " + id + " not found"));
    }
}