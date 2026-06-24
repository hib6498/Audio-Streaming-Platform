package org.hib6498.audio_streaming.entities;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
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

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private TrackStatus trackStatus;

  @Column(name = "cover_art")
  private String coverArt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private Timestamp createdAt;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "track_id")
  private List<TrackUserEntity> features;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "storage_status_id")
  private StorageStatusEntity store;
}
