package com.juny.spacestory.review.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RequestCreateReview(
    @NotNull(message = "content cannot be null.") String content,
    @NotNull(message = "rating cannot be null.") Double rating,
    @NotNull(message = "userId cannot be null.") UUID userId) {}
