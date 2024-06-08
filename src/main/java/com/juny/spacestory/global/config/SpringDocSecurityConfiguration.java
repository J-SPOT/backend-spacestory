package com.juny.spacestory.global.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.servlet.Filter;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.configuration.SpringDocSecurityOAuth2Customizer;
import org.springdoc.core.configuration.hints.SpringDocSecurityHints;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Lazy(false)
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression(
    "${springdoc.api-docs.enabled:true} and ${springdoc.enable-spring-security:true}")
@ConditionalOnClass({SecurityFilterChain.class})
@ConditionalOnWebApplication
// @ConditionalOnBean({SpringDocConfiguration.class})
@ImportRuntimeHints({SpringDocSecurityHints.class})
public class SpringDocSecurityConfiguration {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(
          org.springdoc.core.configuration.SpringDocSecurityConfiguration.class);

  public SpringDocSecurityConfiguration() {}

  static {
    SpringDocUtils.getConfig()
        .addRequestWrapperToIgnore(new Class[] {Authentication.class})
        .addResponseTypeToIgnore(Authentication.class)
        .addAnnotationsToIgnore(new Class[] {AuthenticationPrincipal.class});
  }

  @Lazy(false)
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass({OAuth2AuthorizationService.class})
  class SpringDocSecurityOAuth2Configuration {
    SpringDocSecurityOAuth2Configuration() {}

    @Bean
    @ConditionalOnProperty({"springdoc.show-oauth2-endpoints"})
    @Lazy(false)
    GlobalOpenApiCustomizer springDocSecurityOAuth2Customizer() {
      return new SpringDocSecurityOAuth2Customizer();
    }
  }

  @Lazy(false)
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass({Filter.class})
  class SpringSecurityLoginEndpointConfiguration {
    SpringSecurityLoginEndpointConfiguration() {}

    @Bean
    @Lazy(false)
    OpenApiCustomizer springSecurityLoginEndpointCustomiser(ApplicationContext applicationContext) {
      FilterChainProxy filterChainProxy =
          (FilterChainProxy)
              applicationContext.getBean("springSecurityFilterChain", FilterChainProxy.class);
      return (openAPI) -> {
        Iterator var2 = filterChainProxy.getFilterChains().iterator();

        while (var2.hasNext()) {
          SecurityFilterChain filterChain = (SecurityFilterChain) var2.next();
          Stream var10000 = filterChain.getFilters().stream();
          Objects.requireNonNull(UsernamePasswordAuthenticationFilter.class);
          var10000 = var10000.filter(UsernamePasswordAuthenticationFilter.class::isInstance);
          Objects.requireNonNull(UsernamePasswordAuthenticationFilter.class);
          Optional<UsernamePasswordAuthenticationFilter> optionalFilter =
              var10000.map(UsernamePasswordAuthenticationFilter.class::cast).findAny();
          var10000 = filterChain.getFilters().stream();
          Objects.requireNonNull(DefaultLoginPageGeneratingFilter.class);
          var10000 = var10000.filter(DefaultLoginPageGeneratingFilter.class::isInstance);
          Objects.requireNonNull(DefaultLoginPageGeneratingFilter.class);
          Optional<DefaultLoginPageGeneratingFilter> optionalDefaultLoginPageGeneratingFilter =
              var10000.map(DefaultLoginPageGeneratingFilter.class::cast).findAny();
          if (optionalFilter.isPresent()) {
            UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter =
                (UsernamePasswordAuthenticationFilter) optionalFilter.get();
            Operation operation = new Operation();
            Schema<?> schema =
                (new ObjectSchema())
                    .addProperty(
                        usernamePasswordAuthenticationFilter.getUsernameParameter(),
                        new StringSchema())
                    .addProperty(
                        usernamePasswordAuthenticationFilter.getPasswordParameter(),
                        new StringSchema());
            String mediaType = "application/json";
            if (optionalDefaultLoginPageGeneratingFilter.isPresent()) {
              DefaultLoginPageGeneratingFilter defaultLoginPageGeneratingFilter =
                  (DefaultLoginPageGeneratingFilter) optionalDefaultLoginPageGeneratingFilter.get();
              Field formLoginEnabledField =
                  FieldUtils.getDeclaredField(
                      DefaultLoginPageGeneratingFilter.class, "formLoginEnabled", true);

              try {
                boolean formLoginEnabled =
                    (Boolean) formLoginEnabledField.get(defaultLoginPageGeneratingFilter);
                if (formLoginEnabled) {
                  mediaType = "application/x-www-form-urlencoded";
                }
              } catch (IllegalAccessException var16) {
                IllegalAccessException e = var16;
              }
            }

            RequestBody requestBody =
                (new RequestBody())
                    .content(
                        (new Content()).addMediaType(mediaType, (new MediaType()).schema(schema)));
            operation.requestBody(requestBody);
            ApiResponses apiResponses = new ApiResponses();
            apiResponses.addApiResponse(
                String.valueOf(HttpStatus.OK.value()),
                (new ApiResponse()).description(HttpStatus.OK.getReasonPhrase()));
            apiResponses.addApiResponse(
                String.valueOf(HttpStatus.FORBIDDEN.value()),
                (new ApiResponse()).description(HttpStatus.FORBIDDEN.getReasonPhrase()));
            operation.responses(apiResponses);
            operation
                .addTagsItem("유저 인증 API")
                .summary("로그인 요청 API")
                .description("테스트 email: user, password: 1234")
                .responses(
                    new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("로그인 성공"))
                        .addApiResponse(
                            "E3", new ApiResponse().description("401, 인증에 실패한 경우")));
            PathItem pathItem = (new PathItem()).post(operation);

            try {
              Field requestMatcherField =
                  AbstractAuthenticationProcessingFilter.class.getDeclaredField(
                      "requiresAuthenticationRequestMatcher");
              requestMatcherField.setAccessible(true);
              AntPathRequestMatcher requestMatcher =
                  (AntPathRequestMatcher)
                      requestMatcherField.get(usernamePasswordAuthenticationFilter);
              String loginPath = requestMatcher.getPattern();
              requestMatcherField.setAccessible(false);
              openAPI.getPaths().addPathItem(loginPath, pathItem);
            } catch (IllegalAccessException | ClassCastException | NoSuchFieldException var17) {
              Exception ignored = var17;
            }
          }
        }
      };
    }
  }
}
