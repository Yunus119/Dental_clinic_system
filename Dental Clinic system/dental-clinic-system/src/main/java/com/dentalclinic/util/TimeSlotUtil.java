package com.dentalclinic.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotUtil {

    private static final LocalTime MORNING_START = LocalTime.of(9, 0);
    private static final LocalTime MORNING_END = LocalTime.of(11, 30);   // last morning slot
    private static final LocalTime AFTERNOON_START = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(16, 30); // last afternoon slot
    private static final int SLOT_MINUTES = 30;

    // builds the full list of slot times for one day, in order
    // slot number = position in this list + 1
    public static List<LocalTime> getAllSlotTimes() {

        List<LocalTime> slots = new ArrayList<>();

        // morning slots
        LocalTime current = MORNING_START;
        while (!current.isAfter(MORNING_END)) {
            slots.add(current);
            current = current.plusMinutes(SLOT_MINUTES);
        }

        // afternoon slots
        current = AFTERNOON_START;
        while (!current.isAfter(AFTERNOON_END)) {
            slots.add(current);
            current = current.plusMinutes(SLOT_MINUTES);
        }

        return slots;
    }

    // converts a slot number (1-based) and a date into the actual appointment time
    public static LocalDateTime getDateTimeForSlot(LocalDate date, int slotNumber) {

        List<LocalTime> allSlots = getAllSlotTimes();

        // slot numbers are 1-based, list is 0-based
        int index = slotNumber - 1;

        if (index < 0 || index >= allSlots.size()) {
            throw new IllegalArgumentException("Invalid slot number: " + slotNumber
                    + ". Must be between 1 and " + allSlots.size());
        }

        LocalTime time = allSlots.get(index);
        return LocalDateTime.of(date, time);
    }

    // total number of bookable slots per day
    public static int getMaxSlotsPerDay() {
        return getAllSlotTimes().size();
    }
}