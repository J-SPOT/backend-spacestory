package com.juny.spacestory.global.config;

import com.juny.spacestory.global.security.jwt.refresh.RefreshRepository;
import java.util.Collections;
import com.juny.spacestory.global.security.filter.JwtFilter;
import com.juny.spacestory.global.security.filter.LoginFilter;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;

  public SecurityConfig(
      AuthenticationConfiguration authenticationConfiguration,
      JwtUtil jwtUtil,
      RefreshRepository refreshRepository) {

    this.authenticationConfiguration = authenticationConfiguration;
    this.jwtUtil = jwtUtil;
    this.refreshRepository = refreshRepository;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {

    return configuration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {

    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {

    http.cors(
        (cors) ->
            cors.configurationSource(
                request -> {
                  CorsConfiguration config = new CorsConfiguration();

                  config.setAllowedOrigins(
                      List.of("http://localhost:5173", "https://spacestory.duckdns.org"));

                  config.setAllowedMethods(Collections.singletonList("*"));

                  return config;
                }));

    http.authorizeHttpRequests(
        (auth) ->
            auth.requestMatchers(
                    "/",
                    "/api/v1/auth/login",
                    "/api/**",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/v3/api-docs/**")
                .permitAll()
                .requestMatchers("/admin/**")
                .hasAuthority("ADMIN")
                .anyRequest()
                .authenticated());

    http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);

    http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

    http.addFilterAt(
        new LoginFilter(
            authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository),
        UsernamePasswordAuthenticationFilter.class);

    http.sessionManagement(
        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
