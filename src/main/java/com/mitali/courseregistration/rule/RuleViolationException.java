package com.mitali.courseregistration.rule;

public class RuleViolationException extends RuntimeException {

    private final String rule;

    public RuleViolationException(String rule, String reason) {
        super(reason);
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }
}