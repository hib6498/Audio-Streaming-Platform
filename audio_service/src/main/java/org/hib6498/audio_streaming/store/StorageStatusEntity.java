package org.hib6498.audio_streaming.store;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hib6498.audio_streaming.track.TrackEntity;

@Entity
@Table(name = "storage_status")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class StorageStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(name = "s3_key", nullable = false)
  private String s3Key;

  @OneToOne(mappedBy = "store")
  private TrackEntity track;

  @Column(name = "expires_at", nullable = false, updatable = false)
  private Timestamp expiresAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private UploadStatus status;
}
