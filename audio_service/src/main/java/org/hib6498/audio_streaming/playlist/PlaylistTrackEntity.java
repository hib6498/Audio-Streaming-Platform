package org.hib6498.audio_streaming.playlist;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hib6498.audio_streaming.track.TrackEntity;

@Entity
@Table(name = "playlist_track")
@Data
@Accessors(chain = true)
public class PlaylistTrackEntity {
  @EmbeddedId private PlaylistTrackKey key;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId(value = "trackId")
  @JoinColumn(name = "track_id")
  private TrackEntity track;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId(value = "playlistId")
  @JoinColumn(name = "playlist_id")
  private PlaylistEntity playlist;
}
