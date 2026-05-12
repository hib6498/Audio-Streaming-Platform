package com.akash.auth_microservice.refreshToken;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Embeddable
public class RefreshTokenKey implements Serializable {
  private String token;
  private Long userId;
}
