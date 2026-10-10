package com.mitali.courseregistration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mitali.courseregistration.model.Registration;
import com.mitali.courseregistration.service.RegistrationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    public record RegisterRequest(@NotNull Long studentId, @NotNull Long courseId) {
    }

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Registration register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request.studentId(), request.courseId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void drop(@PathVariable Long id) {
        service.drop(id);
    }
}