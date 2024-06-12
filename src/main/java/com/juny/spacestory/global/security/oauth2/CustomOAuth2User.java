package com.juny.spacestory.global.security.oauth2;

import com.juny.spacestory.user.domain.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

  private String name;
  private UUID id;
  private Role role;
  private String socialId;

  public CustomOAuth2User(String name, UUID id, Role role, String socialId) {
    this.name = name;
    this.id = id;
    this.role = role;
    this.socialId = socialId;
  }

  @Override
  public <A> A getAttribute(String name) {
    return OAuth2User.super.getAttribute(name);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Map.of();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();

    collection.add((GrantedAuthority) () -> this.role.name());

    return collection;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getId() {
    return id.toString();
  }

  public String getSocialId() {
    return socialId;
  }
}
