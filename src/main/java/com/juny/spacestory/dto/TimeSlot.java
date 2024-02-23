package com.juny.spacestory.dto;

import java.time.LocalTime;

public record TimeSlot(LocalTime startTime, LocalTime endTime) {
}
