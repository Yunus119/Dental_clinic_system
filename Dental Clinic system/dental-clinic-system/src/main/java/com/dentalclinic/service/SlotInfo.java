package com.dentalclinic.service;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

// represents one bookable slot on the schedule grid
@Getter
@Setter
@AllArgsConstructor
public class SlotInfo {

    private int slotNumber;
    private LocalTime time;
    private boolean available;
}