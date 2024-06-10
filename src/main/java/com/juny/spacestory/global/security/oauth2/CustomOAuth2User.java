package com.juny.spacestory.global.security.oauth2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

  private final ReqOAuth2User reqOAuth2User;

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

    collection.add((GrantedAuthority) () -> reqOAuth2User.role().name());

    return collection;
  }

  @Override
  public String getName() {
    return reqOAuth2User.name();
  }

  public String getEmail() {
    return reqOAuth2User.email();
  }

  public String getSocialId() {
    return reqOAuth2User.socialId();
  }
}
