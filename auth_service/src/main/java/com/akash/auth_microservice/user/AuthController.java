package com.akash.auth_microservice.user;

import com.akash.auth_microservice.refreshToken.RefreshToken;
import com.akash.auth_microservice.refreshToken.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class AuthController {
  @Autowired private AuthService authService;
  @Autowired private RefreshTokenService refreshTokenService;
  @Autowired private UserService userService;
  @Autowired private UserEventProducer producer;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDTO> registerUser(
      @RequestBody RegisterUserRequestDTO newUser) {
    User registeredUser =
        authService.registerUser(
            newUser.username(), newUser.email(), newUser.displayName(), newUser.password());
    Profile newProfile = userService.initializeProfile(registeredUser);
    UserInfoDTO message =
        new UserInfoDTO(
            registeredUser.getUserId(),
            registeredUser.getDisplayName(),
            registeredUser.getUsername(),
            newProfile.getProfilePic());
    producer.sendUserInfo(message);

    RegisterResponseDTO response =
        new RegisterResponseDTO(
            registeredUser.getUsername(), registeredUser.getEmail(), registeredUser.getCreatedAt());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> loginUser(
      @RequestBody LoginUserDTO user, HttpServletResponse response) {

    Pair<String, RefreshToken> accessRefreshTokens =
        authService.loginAndGenerateTokens(user.username(), user.password());
    RefreshToken refreshToken = accessRefreshTokens.getSecond();

    Duration maxAgeSeconds =
        Duration.between(Instant.now(), refreshToken.getExpiresAt().toInstant());

    ResponseCookie cookie =
        ResponseCookie.from("jwtRT", refreshToken.getToken())
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("LaX")
            .maxAge(maxAgeSeconds)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    LoginResponseDTO loginResponse = new LoginResponseDTO(accessRefreshTokens.getFirst());
    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logoutUser(
      HttpServletResponse response, @AuthenticationPrincipal User principal) {
    refreshTokenService.removeRefreshTokensForUser(principal.getUserId());

    ResponseCookie cookie =
        ResponseCookie.from("jwtRT", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("LaX")
            .maxAge(0)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok("User logged out");
  }

  public record LoginResponseDTO(String accessToken) {}

  public record LoginUserDTO(String username, String password) {}

  public record RegisterResponseDTO(String username, String email, Timestamp createdAt) {}

  public record RegisterUserRequestDTO(
      String username, String email, String password, String displayName) {}
}
