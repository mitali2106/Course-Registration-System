# Course Registration System

A Spring Boot REST API that registers students for courses and rejects invalid registrations with a specific reason, modeled on VIT's FFCS.

## Rules

Every registration passes through 4 validators, each implemented as a separate Strategy class behind a common `RegistrationRule` interface:

| Rule | Rejects when |
|---|---|
| Duplicate | The student is already registered for the course |
| Slot clash | The course meets on a day and period the student is already busy |
| Credit limit | Total credits would exceed the student's maximum |
| Seat cap | The course has reached its capacity |

A rejected registration returns HTTP 409 with the rule and reason, for example:

```json
{"rule":"SLOT_CLASH","reason":"CSE1002 clashes with CSE1001 on MONDAY period 1"}
```

## Tech stack

Java 17, Spring Boot 4, Spring Data JPA (Hibernate), MySQL 8, JUnit 5, H2 for tests.

## Database (5 tables)

- `student`: id, name, reg_no, max_credits
- `slot`: id, code
- `slot_timing`: id, slot_id, day_of_week, period_no
- `course`: id, code, title, credits, capacity, slot_id
- `registration`: id, student_id, course_id, registered_at, unique on (student_id, course_id)

Slot clashes are detected in a single JPQL query that joins the student's registered slot timings against the timings of the requested slot.

## API (8 endpoints)

| Method | Path | Purpose |
|---|---|---|
| POST | `/students` | Add a student |
| GET | `/students/{id}/registrations` | List a student's courses |
| GET | `/students/{id}/timetable` | Weekly timetable sorted by day and period |
| GET | `/students/{id}/credits` | Credits used, allowed and remaining |
| POST | `/courses` | Add a course to a slot |
| GET | `/courses` | List courses with seats left |
| POST | `/registrations` | Register (runs the 4 rules) |
| DELETE | `/registrations/{id}` | Drop a course |

## Run locally

1. Install Java 17+ and MySQL 8.
2. Set the database password as an environment variable: `setx DB_PASS "your_password"` (Windows).
3. Run `./mvnw spring-boot:run`. The app starts on port 8081 and creates the `course_reg` database and tables on first start.

Optional variables: `DB_URL` and `DB_USER`.

## Tests

`./mvnw test` runs 10 JUnit tests on an in-memory H2 database: an allowed and a rejected case for each rule, plus a successful registration and a course drop.