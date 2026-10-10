package com.mitali.courseregistration.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mitali.courseregistration.rule.RuleViolationException;
import com.mitali.courseregistration.service.NotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorResponse(String rule, String reason) {
    }

    @ExceptionHandler(RuleViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRule(RuleViolationException ex) {
        return new ErrorResponse(ex.getRule(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(DataIntegrityViolationException ex) {
        return new ErrorResponse("DUPLICATE_VALUE",
                "A record with that code or registration number already exists");
    }
}