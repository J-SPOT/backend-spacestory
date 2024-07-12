package com.juny.spacestory.space.dto;

import java.time.LocalTime;
import java.util.List;

public record ReqSpace(String name,
                       String description,
                       String reservationNotes,
                       LocalTime openingTime,
                       LocalTime closingTime,
                       Integer hourlyRate,
                       Integer spaceSize,
                       Integer maxCapacity,
                       String mainCategory,
                       List<String> subCategories,
                       List<String> options,
                       List<String> hashtags) {

}
