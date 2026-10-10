package com.mitali.courseregistration;

import java.time.DayOfWeek;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mitali.courseregistration.model.Slot;
import com.mitali.courseregistration.model.SlotTiming;
import com.mitali.courseregistration.repository.SlotRepository;
import com.mitali.courseregistration.repository.SlotTimingRepository;

@Component
public class SlotSeeder implements CommandLineRunner {

    private final SlotRepository slots;
    private final SlotTimingRepository timings;

    public SlotSeeder(SlotRepository slots, SlotTimingRepository timings) {
        this.slots = slots;
        this.timings = timings;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (slots.count() > 0) {
            return;
        }
        add("A1", DayOfWeek.MONDAY, 1, DayOfWeek.WEDNESDAY, 2);
        add("B1", DayOfWeek.TUESDAY, 1, DayOfWeek.THURSDAY, 2);
        add("C1", DayOfWeek.WEDNESDAY, 1, DayOfWeek.FRIDAY, 2);
        add("D1", DayOfWeek.THURSDAY, 1, DayOfWeek.MONDAY, 2);
        add("L1", DayOfWeek.MONDAY, 1, DayOfWeek.MONDAY, 2);
        add("L2", DayOfWeek.TUESDAY, 3, DayOfWeek.TUESDAY, 4);
    }

    private void add(String code, DayOfWeek day1, int period1, DayOfWeek day2, int period2) {
        Slot slot = slots.save(new Slot(code));
        timings.save(new SlotTiming(slot, day1, period1));
        timings.save(new SlotTiming(slot, day2, period2));
    }
}