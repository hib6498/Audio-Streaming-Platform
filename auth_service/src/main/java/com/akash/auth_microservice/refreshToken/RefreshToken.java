package com.akash.auth_microservice.refreshToken;

import com.akash.auth_microservice.user.User;
import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Data;
import lombok.experimental.Accessors;

@Table(name = "refresh_tokens")
@Entity
@Data
@Accessors(chain = true)
public class RefreshToken {

  @Id
  @Column(name = "token", nullable = false, updatable = false)
  private String token;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private User user;

  @Column(name = "expires_at", nullable = false, updatable = false)
  private Timestamp expiresAt;

  @Column(name = "is_revoked", nullable = false)
  private boolean isRevoked;
}
