package com.juny.spacestory.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedHeaders("*")
        .allowedMethods("*")
        .allowedOrigins("http://localhost:5173")
        .exposedHeaders("Set-Cookie")
        .exposedHeaders("refresh")
        .allowCredentials(true);
  }
}
