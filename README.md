# Course Registration System

A Spring Boot REST API, with a timetable page, that registers students for courses and rejects invalid registrations with a specific reason. Modeled on VIT's FFCS.

**Live demo:** http://3.110.224.161:8081/ (deployed on AWS EC2 with RDS MySQL)

## Rules

Every registration passes through 4 validators, each a separate Strategy class behind a common `RegistrationRule` interface, run in order:

| Order | Rule | Rejects when |
|---|---|---|
| 1 | Duplicate | The student is already registered for the course |
| 2 | Slot clash | The course meets on a day and period the student is already busy |
| 3 | Credit limit | Total credits would exceed the student's maximum |
| 4 | Seat cap | The course has reached its capacity |

A rejected registration returns HTTP 409 with the rule and reason:

```json
{"rule":"SLOT_CLASH","reason":"CSE1002 clashes with CSE1001 on MONDAY period 1"}
```

Adding a new rule means writing one new class; the service does not change.

## Tech stack

Java 17, Spring Boot 4, Spring Data JPA (Hibernate), MySQL 8, JUnit 5, H2 (tests), AWS EC2, AWS RDS.

## Database (5 tables)

| Table | Columns |
|---|---|
| `student` | id, name, reg_no (unique), max_credits |
| `slot` | id, code (unique) |
| `slot_timing` | id, slot_id, day_of_week, period_no |
| `course` | id, code (unique), title, credits, capacity, slot_id |
| `registration` | id, student_id, course_id, registered_at; unique on (student_id, course_id) |

Slot clashes are detected in a single JPQL query that joins the student's registered slot timings against the timings of the requested slot.

Seats left are calculated as capacity minus the count of registrations, so there is no counter to fall out of sync.

## API (9 endpoints)

| Method | Path | Purpose |
|---|---|---|
| GET | `/students` | List all students |
| POST | `/students` | Add a student |
| GET | `/students/{id}/registrations` | List a student's courses |
| GET | `/students/{id}/timetable` | Weekly timetable sorted by day and period |
| GET | `/students/{id}/credits` | Credits used, allowed and remaining |
| POST | `/courses` | Add a course to a slot |
| GET | `/courses` | List courses with seats left and slot timings |
| POST | `/registrations` | Register (runs the 4 rules) |
| DELETE | `/registrations/{id}` | Drop a course |

## Student page

The home page (`/`) shows a student's weekly timetable. Pick a student, register or drop courses, and hover a course to see where it lands; clashing periods are outlined in red before you register. Seat counts refresh every 5 seconds.

## Run locally

1. Install Java 17+ and MySQL 8.
2. Set the database password: `setx DB_PASS "your_password"` (Windows), then reopen the terminal.
3. Run `./mvnw spring-boot:run`.
4. Open http://localhost:8081/. The `course_reg` database, tables and timetable slots are created on first start.

Optional environment variables: `DB_URL`, `DB_USER`.

## Tests

`./mvnw test` runs 10 JUnit tests on an in-memory H2 database: an allowed and a rejected case for each rule, plus a successful registration and a course drop.

## Deployment

- **App:** EC2 t3.micro running Amazon Linux 2023 and Java 17, started with `java -jar` using the JAR from the v1.0 release.
- **Database:** RDS MySQL db.t4g.micro, private, reachable only from the EC2 instance's security group.
- **Configuration:** database URL, username and password are passed as environment variables, so no credentials are in the code.

## Known limitation

Two simultaneous requests for the last seat can both pass the seat check before either is saved. The fix is optimistic locking (a version column on `course`) or a row lock while counting.