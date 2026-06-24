package org.hib6498.audio_streaming.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


//dont know why the hell i used userdetails
//TODO: get rid of userdetails interface
@Data
@Entity
@Table(name = "user_info_replica")
@Accessors(chain = true)
public class UserInfoEntity implements UserDetails {
  @Id
  @Column(name = "user_id", updatable = false)
  private Long userId;

  @Column(name = "username", updatable = false)
  private String username;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "profile_pic")
  private String profilePic;

  @Override
  @NonNull
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return "";
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}
