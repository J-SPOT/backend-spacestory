package com.juny.spacestory.global.security.service;

import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

    if (user != null) {

      return new CustomUserDetails(user);
    }

    return null;
  }
}
