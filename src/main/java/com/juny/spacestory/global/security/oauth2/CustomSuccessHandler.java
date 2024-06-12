package com.juny.spacestory.global.security.oauth2;

import static com.juny.spacestory.global.security.jwt.JwtUtil.ACCESS_TOKEN_EXPIRAION;
import static com.juny.spacestory.global.security.jwt.JwtUtil.ACCESS_TOKEN_KEY;
import static com.juny.spacestory.global.security.jwt.JwtUtil.ACCESS_TOKEN_PREFIX;
import static com.juny.spacestory.global.security.jwt.JwtUtil.CHARACTER_ENCODING;
import static com.juny.spacestory.global.security.jwt.JwtUtil.CONTENT_TYPE;
import static com.juny.spacestory.global.security.jwt.JwtUtil.REFRESH_TOKEN_EXPIRAION;
import static com.juny.spacestory.global.security.jwt.JwtUtil.REFRESH_TOKEN_KEY;
import static com.juny.spacestory.global.security.jwt.JwtUtil.REFRESH_TOKEN_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.jwt.refresh.Refresh;
import com.juny.spacestory.global.security.jwt.refresh.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) throws IOException, ServletException {

    log.info("onAuthenticationSuccess");

    CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

    String id = customUserDetails.getId();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();

    System.out.println("id = " + id);
    System.out.println("role = " + role);

    String accessToken = jwtUtil.createJwt(ACCESS_TOKEN_PREFIX, id, role);
    String refreshToken = jwtUtil.createJwt(REFRESH_TOKEN_PREFIX, id, role);

    String accessTokenExpired =
      jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(accessToken));
    String refreshTokenExpired =
      jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(refreshToken));

    refreshRepository.save(new Refresh(UUID.fromString(id), refreshToken, refreshTokenExpired));

    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put(ACCESS_TOKEN_KEY, accessToken);
    responseBody.put(REFRESH_TOKEN_KEY, refreshToken);
    responseBody.put(ACCESS_TOKEN_EXPIRAION, accessTokenExpired);
    responseBody.put(REFRESH_TOKEN_EXPIRAION, refreshTokenExpired);

    String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);

    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    PrintWriter writer = response.getWriter();
    writer.write("<html><body>");
    writer.write("<script>");
    writer.write("const response = " + jsonResponse + ";");
    writer.write("if (window.opener) {");
    writer.write("  window.opener.postMessage(response, '*');");
    writer.write("  window.close();");
    writer.write("} else {");
    writer.write("  console.error('No window.opener available');");
    writer.write("}");
    writer.write("</script>");
    writer.write("</body></html>");
    writer.flush();
//    response.sendRedirect("localhost:5173asdkfjasdklfjsadklfjsadlkj");

//    PrintWriter writer = response.getWriter();
//    writer.write(new ObjectMapper().writeValueAsString(responseBody));
//    writer.flush();
  }
}
