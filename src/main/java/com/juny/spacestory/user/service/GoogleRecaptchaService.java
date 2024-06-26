package com.juny.spacestory.user.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class GoogleRecaptchaService {

  private final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

  private final RestTemplate restTemplate;

  @Value("${CAPTCHA_SECRET_KEY}")
  private String secretKey;

  public boolean verifyRecaptcha(String recaptchaToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
    map.add("secret", secretKey);
    map.add("response", recaptchaToken);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    Map<String, Object> response = restTemplate.postForObject(RECAPTCHA_VERIFY_URL, request, Map.class);

    return response != null && (Boolean) response.get("success");
  }
}
