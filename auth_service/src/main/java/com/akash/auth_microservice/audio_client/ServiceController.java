package com.akash.auth_microservice.audio_client;

import com.akash.auth_microservice.user.User;
import com.akash.auth_microservice.user.UserInfoDTO;
import com.akash.auth_microservice.user.UserLookupResult;
import com.akash.auth_microservice.user.UserService;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
public class ServiceController {
  @Autowired private ServiceAuthService serviceAuthService;
  @Autowired private UserService userService;

  @PostMapping("/login")
  public ResponseEntity<ClientLoginResponseDTO> loginService(
      @RequestBody ClientLoginRequestDTO loginDetails) {
    Pair<String, Date> accessAndExpirationDate =
        serviceAuthService.loginAndGenerateTokens(loginDetails.id(), loginDetails.secret());
    return ResponseEntity.ok(
        new ClientLoginResponseDTO(
            accessAndExpirationDate.getFirst(), accessAndExpirationDate.getSecond().toInstant()));
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Long id) {
    User user = userService.loadUserById(id);

    UserInfoDTO response =
        new UserInfoDTO(user.getUserId(), user.getDisplayName(), user.getUsername(), "");

    return ResponseEntity.ok(response);
  }

  @PostMapping("/users")
  public ResponseEntity<ManyUsersInfoResponseDTO> getUsersInfo(
      @RequestBody ManyUsersInfoRequestDTO requestIds) {
    UserLookupResult result = userService.loadUsersByIds(requestIds.ids());

    List<UserInfoDTO> users =
        result.users().stream()
            .map(
                user ->
                    new UserInfoDTO(
                        user.getUserId(), user.getDisplayName(), user.getUsername(), ""))
            .toList();

    return ResponseEntity.ok(new ManyUsersInfoResponseDTO(users, result.remaining()));
  }

  public record ClientLoginResponseDTO(String accessToken, Instant expiresAt) {}

  public record ClientLoginRequestDTO(String id, String secret) {}

  public record ManyUsersInfoResponseDTO(List<UserInfoDTO> users, List<Long> remaining) {}

  public record ManyUsersInfoRequestDTO(List<Long> ids) {}
}
