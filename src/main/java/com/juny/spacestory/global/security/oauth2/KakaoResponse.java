package com.juny.spacestory.global.security.oauth2;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

  private final Map<String, Object> attribute;
  private String providerId;

  public KakaoResponse(Map<String, Object> attribute) {

    providerId = attribute.get("id").toString();

    this.attribute = (Map<String, Object>) attribute.get("kakao_account");
  }

  @Override
  public String getProvider() {

    return "kakao";
  }

  @Override
  public String getProviderId() {

    return providerId;
  }

  @Override
  public String getEmail() {

    return attribute.get("email").toString();
  }

  @Override
  public String getName() {

    Map<String, Object> profile = (Map<String, Object>) attribute.get("profile");

    return profile.get("nickname").toString();
  }
}
