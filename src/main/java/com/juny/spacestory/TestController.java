package com.juny.spacestory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;

@RestController
public class TestController {

  @GetMapping("/api/v1/hello")
  public String getHello() {

    return "Get Hello";
  }

  @GetMapping("/api/set-cookie")
  public String setCookie(HttpServletResponse response) {

    Cookie cookie = new Cookie("refreshToken", "1234");
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

    response.addCookie(cookie);

    return "Cookie set successfully";
  }

  @PostMapping("/api/v1/hello")
  public String postHello() {

    return "Post Hello";
  }

  @PatchMapping("/api/v1/hello")
  public String patchHello() {

    return "Patch Hello";
  }

  @GetMapping("/api/v1/test")
  public String testHello() {

    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    Authentication authenticationhaha = SecurityContextHolder.getContext().getAuthentication();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iter = authorities.iterator();
    GrantedAuthority auth = iter.next();
    String role = auth.getAuthority();
    System.out.println("name = " + name);
    System.out.println("role = " + role);

    return "test Hello";
  }
}
