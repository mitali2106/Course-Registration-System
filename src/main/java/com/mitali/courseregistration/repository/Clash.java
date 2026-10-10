package com.mitali.courseregistration.repository;

import java.time.DayOfWeek;

public record Clash(String courseCode, DayOfWeek dayOfWeek, int periodNo) {
}