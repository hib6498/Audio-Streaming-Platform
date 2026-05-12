package org.hib6498.audio_streaming.entities;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

@Table(name = "album")
@Entity
@Data
@Accessors(chain = true)
public class AlbumEntity {
  @Id
  @Column(name = "album_id", updatable = false)
  private Long playlistId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "release_date")
  private Date releaseDate;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Timestamp createdAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserInfoEntity userInfo;

  @Column(name = "description")
  private String description;

  @Column(name = "cover_art")
  private String coverArt;
}
