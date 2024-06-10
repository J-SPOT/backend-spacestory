package com.juny.spacestory.global.security.oauth2;

import com.juny.spacestory.user.domain.Role;

public record ReqOAuth2User(String name, String email, Role role, String socialId) {}
