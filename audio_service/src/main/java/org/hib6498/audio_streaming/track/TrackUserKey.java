package org.hib6498.audio_streaming.track;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@Accessors(chain = true)
@Embeddable
@Setter
public class TrackUserKey implements Serializable {
  private Long trackId;
  private Long userId;
}
