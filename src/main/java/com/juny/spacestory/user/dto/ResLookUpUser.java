package com.juny.spacestory.user.dto;

import com.juny.spacestory.user.domain.Role;

public record ResLookUpUser(String name, String email, Long point, Role role, boolean isTotpEnabled) {

}
