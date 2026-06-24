package org.hib6498.audio_streaming.playlist;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@Accessors(chain = true)
@Embeddable
@Setter
@Getter
public class PlaylistTrackKey implements Serializable {
  private Long playlistId;
  private Long trackId;
}
