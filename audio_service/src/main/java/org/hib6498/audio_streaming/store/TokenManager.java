package org.hib6498.audio_streaming.store;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

// note: i dont think this will be used anymore
// this was used before rabbitmq was introduced in this project.
// but just in case if queue is busy, these synchronous calls shall be used
@Component
public class TokenManager {

  private final WebClient loginClient;

  @Value("${CLIENT_ID}")
  private String clientId;

  @Value("${CLIENT_SECRET}")
  private String secret;

  @Getter private String accessToken;

  private Instant expiresAt;

  public TokenManager(@Qualifier("loginWebClient") WebClient loginClient) {
    this.loginClient = loginClient;
  }

  public synchronized String getToken() {
    if (accessToken == null
        || expiresAt == null
        || Instant.now().isAfter(expiresAt.minusSeconds(30))) {

      refreshToken();
    }

    return accessToken;
  }

  public synchronized void refreshToken() {

    TokenResponse response =
        loginClient
            .post()
            .uri("/service/login")
            .bodyValue(
                Map.of(
                    "id", clientId,
                    "secret", secret))
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();

    if (response != null) {
      this.accessToken = response.accessToken();
      this.expiresAt = response.expiresAt();
    }
  }

  private record TokenResponse(String accessToken, Instant expiresAt) {}
}
