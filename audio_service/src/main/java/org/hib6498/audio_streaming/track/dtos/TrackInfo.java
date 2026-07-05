package org.hib6498.audio_streaming.track.dtos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hib6498.audio_streaming.track.TrackEntity;
import org.hib6498.audio_streaming.track.TrackRole;
import org.hib6498.audio_streaming.track.TrackUserEntity;
import org.hib6498.audio_streaming.track.daos.TrackArtistQueryResult;
import org.hib6498.audio_streaming.user.UserIdDisplayNameDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
@Accessors(chain = true)
public class TrackInfo {
  private Long trackId;
  private String title;
  private Timestamp createdAt;
  private UserIdDisplayNameDTO owns;
  private List<UserIdDisplayNameDTO> features;
  private String artworkUrl;

  public static TrackInfo from(TrackEntity track) {
    TrackInfo info =
        new TrackInfo()
            .setTrackId(track.getTrackId())
            .setArtworkUrl(craftArtworkUrl(track.getTrackId()))
            .setCreatedAt(track.getCreatedAt())
            .setTitle(track.getTitle())
            .setFeatures(new ArrayList<>());

    for (TrackUserEntity feature : track.getFeatures()) {
      UserIdDisplayNameDTO user =
          new UserIdDisplayNameDTO(
              feature.getUserInfo().getUserId(), feature.getUserInfo().getDisplayName());

      info.getFeatures().add(user);
      if (feature.getRole() == TrackRole.OWNER) info.setOwns(user);
    }

    return info;
  }

  public static TrackInfo from(TrackArtistQueryResult result, Long trackId) {
    return new TrackInfo()
        .setTrackId(trackId)
        .setTitle(result.title())
        .setArtworkUrl(craftArtworkUrl(trackId))
        .setCreatedAt(result.createdAt())
        .setFeatures(new ArrayList<>());
  }

  private static String craftArtworkUrl(Long trackId) {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/track/{id}/artwork")
        .buildAndExpand(trackId)
        .toUriString();
  }
}
