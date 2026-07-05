package org.hib6498.audio_streaming.playlist;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hib6498.audio_streaming.user.UserInfoEntity;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private UserInfoEntity userInfo;

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "playlist")
  private List<PlaylistTrackEntity> tracks;

  @Column(name = "description")
  private String description;

  @Column(name = "cover_art")
  private String coverArt;
}
