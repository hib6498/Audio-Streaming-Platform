package com.akash.auth_microservice.audio_client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table(name = "service_credentials")
@Entity
@Data
@Accessors(chain = true)
public class Service implements UserDetails {
  @Id
  @Column(name = "client_id")
  private String clientId;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "secret", nullable = false)
  private String secret;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return secret;
  }

  @Override
  public String getUsername() {
    return clientId;
  }
}
