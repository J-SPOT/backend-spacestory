package com.juny.spacestory.user.dto;

import com.juny.spacestory.user.domain.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResLookUpUsers(UUID id, String name, String email, Long point,
                             LocalDateTime deletedAt, Role role, String socialId,
                             boolean isTotpEnabled,
                             List<String> ipAddresses) {
}
