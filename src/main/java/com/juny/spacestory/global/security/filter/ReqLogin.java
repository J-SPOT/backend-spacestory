package com.juny.spacestory.global.security.filter;

public record ReqLogin(
  String email,
  String password
) {}
