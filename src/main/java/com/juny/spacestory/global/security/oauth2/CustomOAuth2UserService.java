package com.juny.spacestory.global.security.oauth2;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserDuplicatedEmailException;
import com.juny.spacestory.user.domain.Role;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    log.info("loadUser");
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    System.out.println("registrationId = " + registrationId);
    log.info("OAuth2User.getAttributes() = {} ", oAuth2User.getAttributes());

    Map<String, Object> attributes = oAuth2User.getAttributes();

    OAuth2Response oAuth2Response = null;
    if (registrationId.equals("naver")) {

      oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
    }
    else if (registrationId.equals("google")) {

      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
    }
    else if (registrationId.equals("kakao")) {

      oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
    } else {

      return null;
    }
    String socialId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

    ReqOAuth2User reqOAuth2User =
        new ReqOAuth2User(oAuth2Response.getName(), oAuth2Response.getEmail(), Role.USER, socialId);

    if (userRepository.findByEmail(oAuth2Response.getEmail()).isPresent()) {

      log.error("중복된 메일입니다.");
      throw new UserDuplicatedEmailException(ErrorCode.USER_DUPLICATED_EMAIL);
    }

    Optional<User> user = userRepository.findBySocialId(socialId);

    user.ifPresentOrElse(
        u -> {
          u.updateNameAndEmail(reqOAuth2User.name(), reqOAuth2User.email());
          userRepository.save(u);
        },
        () -> {
          User u = new User(reqOAuth2User.name(), reqOAuth2User.email(), Role.USER, socialId);
          userRepository.save(u);
        });

    return new CustomOAuth2User(reqOAuth2User);
  }
}
