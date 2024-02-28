package com.juny.spacestory.dto;

import jakarta.validation.constraints.NotNull;

public record RequestUpdateReview(
        @NotNull(message = "content cannot be null.")
        String content,
        @NotNull(message = "rating cannot be null.")
        Double rating
) {
}
