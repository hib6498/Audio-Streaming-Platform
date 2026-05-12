package com.akash.auth_microservice.refreshToken;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  int deleteByExpiresAtBefore(Instant now);

  void deleteByUser_UserId(Long userId);
}
