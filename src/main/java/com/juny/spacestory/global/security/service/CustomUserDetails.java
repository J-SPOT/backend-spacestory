package com.juny.spacestory.global.security.service;

import com.juny.spacestory.user.domain.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final User user;

  public CustomUserDetails(User user) {

    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> collection = new ArrayList<>();

    collection.add((GrantedAuthority) () -> user.getRole().name());

    return collection;
  }

  @Override
  public String getPassword() {

    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getName();
  }

  @Override
  public boolean isAccountNonExpired() {

    return true;
  }

  @Override
  public boolean isAccountNonLocked() {

    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {

    return true;
  }

  @Override
  public boolean isEnabled() {

    return true;
  }

  public String getEmail() {

    return user.getEmail();
  }

  public String getId() {

    return user.getId().toString();
  }

  public List<String> getIpAddresses() {

    return user.getIpAddresses();
  }

  public Boolean getIsTotpEnabled() {

    return user.getIsTotpEnabled();
  }
}
