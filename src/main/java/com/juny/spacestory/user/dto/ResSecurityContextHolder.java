package com.juny.spacestory.user.dto;

public record ResSecurityContextHolder(String id, String email, boolean totpEnabled,
                                       java.util.List<String> ipAddresses,
                                       String role) {

}
