package com.mitali.courseregistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mitali.courseregistration.model.Course;
import com.mitali.courseregistration.model.Registration;
import com.mitali.courseregistration.model.Slot;
import com.mitali.courseregistration.model.Student;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByStudentAndCourse(Student student, Course course);

    long countByCourse(Course course);

    List<Registration> findByStudent(Student student);

    @Query("""
            select coalesce(sum(c.credits), 0)
            from Registration r
            join r.course c
            where r.student = :student
            """)
    long sumCreditsByStudent(@Param("student") Student student);

    @Query("""
            select new com.mitali.courseregistration.repository.Clash(c.code, mine.dayOfWeek, mine.periodNo)
            from Registration r
            join r.course c
            join SlotTiming mine on mine.slot = c.slot
            join SlotTiming wanted on wanted.dayOfWeek = mine.dayOfWeek and wanted.periodNo = mine.periodNo
            where r.student = :student and wanted.slot = :slot
            """)
    List<Clash> findClashes(@Param("student") Student student, @Param("slot") Slot slot);
}