package org.hib6498.audio_streaming.entities;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

@Table(name = "playlist")
@Entity
@Data
@Accessors(chain = true)
public class PlaylistEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "playlist_id", updatable = false)
  private Long playlistId;

  @Column(name = "title", nullable = false)
  private String title;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Timestamp createdAt;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private UserInfoEntity userInfo;

  @Column(name = "description")
  private String description;

  @Column(name = "cover_art")
  private String coverArt;
}
