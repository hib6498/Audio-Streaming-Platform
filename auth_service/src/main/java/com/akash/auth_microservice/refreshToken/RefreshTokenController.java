package com.akash.auth_microservice.refreshToken;

import com.akash.auth_microservice.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class RefreshTokenController {
  @Autowired private RefreshTokenService refreshTokenService;
  @Autowired private JWTUtils jwtUtils;

  @PostMapping("/refresh")
  public ResponseEntity<RefreshTokenResponseDTO> refreshToken(
      HttpServletRequest request, HttpServletResponse response) {
    RefreshToken newRefreshToken = refreshTokenService.validateAndRotateRefreshToken(request);
    String accessToken = jwtUtils.generateAccessToken(newRefreshToken.getUser().getUserId());

    RefreshTokenResponseDTO refreshResponse = new RefreshTokenResponseDTO(accessToken);

    Duration maxAgeSeconds =
        Duration.between(Instant.now(), newRefreshToken.getExpiresAt().toInstant());

    ResponseCookie cookie =
        ResponseCookie.from("jwtRT", newRefreshToken.getToken())
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("LaX")
            .maxAge(maxAgeSeconds)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.ok(refreshResponse);
  }

  public record RefreshTokenResponseDTO(String accessToken) {}
}
