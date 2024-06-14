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
  private final String REGISTRATION_NAVER = "NAVER";
  private final String REGISTRATION_GOOGLE = "GOOGLE";
  private final String REGISTRATION_KAKAO = "KAKAO";



  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    log.info("OAuth2User.getAttributes() = {} ", oAuth2User.getAttributes());

    Map<String, Object> attributes = oAuth2User.getAttributes();

    OAuth2Response oAuth2Response = null;
    if (registrationId.equals(REGISTRATION_GOOGLE)) {

      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
    }
    else if (registrationId.equals(REGISTRATION_NAVER)) {

      oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
    }
    else if (registrationId.equals(REGISTRATION_KAKAO)) {

      oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
    } else {

      return null;
    }
    String socialId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    String name = oAuth2Response.getName();
    String email = oAuth2Response.getEmail();

    if (userRepository.findByEmailAndSocialIdNot(oAuth2Response.getEmail(), email).isPresent()) {

      log.error("Already done a social login with another account.");
      throw new UserDuplicatedEmailException(ErrorCode.USER_DUPLICATED_EMAIL);
    }

    Optional<User> user = userRepository.findBySocialId(socialId);

    user.ifPresentOrElse(
        u -> {
          u.updateNameAndEmail(name, email);
          userRepository.save(u);
        },
        () -> {
          User u = new User(name, email, Role.USER, socialId);
          userRepository.save(u);
        });

    return new CustomOAuth2User(name, user.get().getId(), Role.USER, socialId);
  }
}
