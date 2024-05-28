package com.juny.spacestory.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

  http
      .cors((cors) -> cors
          .configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();

            config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
            config.setAllowedMethods(Collections.singletonList("*"));

            return config;
          }));

    http.authorizeHttpRequests(
        (auth) -> auth.requestMatchers("/", "/login", "/api/v1/hello").permitAll().anyRequest().authenticated());

    http.csrf(csrf -> csrf.disable());

    return http.build();
  }

}
