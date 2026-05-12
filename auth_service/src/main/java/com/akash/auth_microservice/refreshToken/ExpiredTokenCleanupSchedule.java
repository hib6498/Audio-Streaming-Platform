package com.akash.auth_microservice.refreshToken;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiredTokenCleanupSchedule {
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Scheduled(cron = "@weekly")
  public void deleteExpiredTokens() {
    refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
  }
}
