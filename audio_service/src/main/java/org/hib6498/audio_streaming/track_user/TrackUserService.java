package org.hib6498.audio_streaming.track_user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hib6498.audio_streaming.entities.TrackEntity;
import org.hib6498.audio_streaming.entities.TrackUserEntity;
import org.hib6498.audio_streaming.entities.TrackUserKey;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.hib6498.audio_streaming.respositories.TrackUserRepository;
import org.hib6498.audio_streaming.user.TracksForArtistDTO;
import org.hib6498.audio_streaming.user.UserIdDisplayNameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackUserService {
  @Autowired private TrackUserRepository trackUserRepository;

  public List<TracksForArtistDTO> getAllTracks() {
    List<TrackArtistQueryResult> results = trackUserRepository.findAllTracksWithArtists();
    return convertTAQueryResultToTFADTO(results);
  }

  private List<TracksForArtistDTO> convertTAQueryResultToTFADTO(
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

      if (result.isFeature()) {
        trackInfo.getFeatures().add(artist);
      } else {
        trackInfo.setOwns(artist);
      }
    }

    return transformedInfo.entrySet().stream()
        .map(entry -> new TracksForArtistDTO(entry.getKey(), entry.getValue()))
        .toList();
  }

  public List<TracksForArtistDTO> findTracksForUser(Long userId) {
    List<TrackArtistQueryResult> results = trackUserRepository.findTracksWithArtists(userId);
    return convertTAQueryResultToTFADTO(results);
  }

  public List<TrackUserEntity> saveFeaturesForTrack(
      TrackEntity track, List<UserInfoEntity> featuresInfo, Long notFeaturedId) {
    List<TrackUserEntity> features =
        featuresInfo.stream()
            .map(
                user ->
                    new TrackUserEntity()
                        .setKey(
                            new TrackUserKey()
                                .setTrackId(track.getTrackId())
                                .setUserId(user.getUserId()))
                        .setTrack(track)
                        .setFeature(!user.getUserId().equals(notFeaturedId))
                        .setUserInfo(user))
            .toList();

    return trackUserRepository.saveAll(features);
  }
}
