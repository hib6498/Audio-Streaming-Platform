package org.hib6498.audio_streaming.entities;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

@Table(name = "tracks")
@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "trackId")
public class TrackEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "track_id", updatable = false)
  private Long trackId;

  @Column(name = "title", nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private UserInfoEntity userInfo;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "album_id")
  private AlbumEntity album;

  @Column(name = "storage_loc", nullable = false)
  private String storageLoc;

  @Column(name = "cover_art")
  private String coverArt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private Timestamp createdAt;
}
