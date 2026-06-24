package org.hib6498.audio_streaming.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hib6498.audio_streaming.track.TrackEntity;

@Entity
@Data
@Table(name = "track_artists")
@Accessors(chain = true)
public class TrackUserEntity {
  @EmbeddedId private TrackUserKey key;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId(value = "trackId")
  @JoinColumn(name = "track_id")
  private TrackEntity track;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId(value = "userId")
  @JoinColumn(name = "user_id")
  private UserInfoEntity userInfo;

  @Column(name = "role", nullable = false)
  private TrackRole role;
}
