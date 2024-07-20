package com.juny.spacestory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/api/v1/hello")
  public String getHello() {

    return "Get Hello";
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

    return "test Hello";
  }
}
