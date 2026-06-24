package org.hib6498.audio_streaming.shared;

import org.hib6498.audio_streaming.entities.TrackRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackArtistQueryConverter {
  public static List<TracksForArtistDTO> convertTAQueryResultToTFADTO(
      List<TrackArtistQueryResult> results) {
    Map<Long, TrackInfo> transformedInfo = new HashMap<>();

    for (TrackArtistQueryResult result : results) {
      Long trackId = result.trackId();

      TrackInfo trackInfo =
          transformedInfo.computeIfAbsent(
              trackId,
              id ->
                  new TrackInfo()
                      .setTitle(result.title())
                      .setArtworkUrl("http://localhost:8090/api/v1/track/" + trackId + "/artwork")
                      .setCreatedAt(result.createdAt())
                      .setAudioUrl("http://localhost:8090/api/v1/track/" + trackId)
                      .setFeatures(new ArrayList<>()));

      UserIdDisplayNameDTO artist = new UserIdDisplayNameDTO(result.userId(), result.displayName());

      if (result.role() == TrackRole.FEATURE) {
        trackInfo.getFeatures().add(artist);
      } else {
        trackInfo.setOwns(artist);
      }
    }

    return transformedInfo.entrySet().stream()
        .map(entry -> new TracksForArtistDTO(entry.getKey(), entry.getValue()))
        .toList();
  }
}
