package com.akash.auth_microservice.user;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "profile")
@Getter
@Setter
@Accessors(chain = true)
public class Profile {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "fullname")
  private String fullname;

  @Column(name = "description")
  private String description;

  @Column(name = "profile_pic")
  private String profilePic;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Timestamp updatedAt;
}
