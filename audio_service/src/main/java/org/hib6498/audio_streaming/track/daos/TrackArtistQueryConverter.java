package org.hib6498.audio_streaming.track.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hib6498.audio_streaming.track.TrackRole;
import org.hib6498.audio_streaming.track.dtos.TrackInfo;
import org.hib6498.audio_streaming.track.dtos.TracksForArtist;
import org.hib6498.audio_streaming.user.UserIdDisplayNameDTO;

public class TrackArtistQueryConverter {
  public static List<TracksForArtist> convertTAQueryResultToTFADTO(
      List<TrackArtistQueryResult> results) {
    Map<Long, TrackInfo> transformedInfo = new HashMap<>();

    for (TrackArtistQueryResult result : results) {
      Long trackId = result.trackId();

      TrackInfo trackInfo =
          transformedInfo.computeIfAbsent(trackId, id -> TrackInfo.from(result, id));

      UserIdDisplayNameDTO artist = new UserIdDisplayNameDTO(result.userId(), result.displayName());

      trackInfo.getFeatures().add(artist);
      if (result.role() == TrackRole.OWNER) trackInfo.setOwns(artist);
    }

    return transformedInfo.entrySet().stream()
        .map(entry -> new TracksForArtist(entry.getKey(), entry.getValue()))
        .toList();
  }
}
