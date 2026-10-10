package com.mitali.courseregistration.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Slot;
import com.mitali.courseregistration.repository.CourseRepository;
import com.mitali.courseregistration.repository.RegistrationRepository;
import com.mitali.courseregistration.repository.SlotRepository;
import com.mitali.courseregistration.service.NotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/courses")
public class CourseController {

    public record CreateCourseRequest(@NotBlank String code, @NotBlank String title, @Min(1) int credits,
            @Min(1) int capacity, @NotBlank String slotCode) {
    }

    public record CourseView(Long id, String code, String title, int credits, int capacity, String slot,
            long seatsLeft) {
    }

    private final CourseRepository courses;
    private final SlotRepository slots;
    private final RegistrationRepository registrations;

    public CourseController(CourseRepository courses, SlotRepository slots,
            RegistrationRepository registrations) {
        this.courses = courses;
        this.slots = slots;
        this.registrations = registrations;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseView create(@Valid @RequestBody CreateCourseRequest request) {
        Slot slot = slots.findByCode(request.slotCode())
                .orElseThrow(() -> new NotFoundException("Slot " + request.slotCode() + " not found"));
        Course course = courses.save(new Course(request.code(), request.title(), request.credits(),
                request.capacity(), slot));
        return toView(course);
    }

    @GetMapping
    public List<CourseView> list() {
        return courses.findAll().stream().map(this::toView).toList();
    }

    private CourseView toView(Course course) {
        long seatsLeft = course.getCapacity() - registrations.countByCourse(course);
        return new CourseView(course.getId(), course.getCode(), course.getTitle(), course.getCredits(),
                course.getCapacity(), course.getSlot().getCode(), seatsLeft);
    }
}