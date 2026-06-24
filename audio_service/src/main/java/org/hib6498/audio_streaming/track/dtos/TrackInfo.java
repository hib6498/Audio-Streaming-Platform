package org.hib6498.audio_streaming.shared;

import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hib6498.audio_streaming.track.TrackEntity;
import org.hib6498.audio_streaming.entities.TrackRole;
import org.hib6498.audio_streaming.track.TrackUserEntity;

@Data
@Accessors(chain = true)
public class TrackInfo {
  private String title;
  private Timestamp createdAt;
  private UserIdDisplayNameDTO owns;
  private List<UserIdDisplayNameDTO> features;
  private String artworkUrl;
  private String audioUrl;

  public static TrackInfo from(TrackEntity track, String url) {
    TrackInfo info =
        new TrackInfo()
            .setArtworkUrl("")
            .setAudioUrl(url)
            .setCreatedAt(track.getCreatedAt())
            .setTitle(track.getTitle())
            .setFeatures(List.of());

    for (TrackUserEntity feature : track.getFeatures()) {
      UserIdDisplayNameDTO user =
          new UserIdDisplayNameDTO(
              feature.getUserInfo().getUserId(), feature.getUserInfo().getDisplayName());

      if (feature.getRole() == TrackRole.FEATURE) info.getFeatures().add(user);
      else info.setOwns(user);
    }

    return info;
  }
}
