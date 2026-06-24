package org.hib6498.audio_streaming.track_user;

import static org.hib6498.audio_streaming.shared.TrackArtistQueryConverter.convertTAQueryResultToTFADTO;

import java.util.List;
import java.util.Objects;
import org.hib6498.audio_streaming.entities.*;
import org.hib6498.audio_streaming.respositories.TrackUserRepository;
import org.hib6498.audio_streaming.shared.TrackArtistQueryResult;
import org.hib6498.audio_streaming.shared.TracksForArtistDTO;
import org.hib6498.audio_streaming.track.TrackEntity;
import org.hib6498.audio_streaming.track.TrackUserEntity;
import org.hib6498.audio_streaming.track.TrackUserKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackUserService {
  @Autowired private TrackUserRepository trackUserRepository;

  public List<TracksForArtistDTO> getAllTracks() {
    List<TrackArtistQueryResult> results = trackUserRepository.findAllTracksWithArtists();
    return convertTAQueryResultToTFADTO(results);
  }

  public List<TracksForArtistDTO> findTracksForUser(Long userId) {
    List<TrackArtistQueryResult> results = trackUserRepository.findTracksWithArtists(userId);
    return convertTAQueryResultToTFADTO(results);
  }

  public List<TrackUserEntity> saveFeaturesForTrack(
          TrackEntity track, List<UserInfoEntity> featuresInfo, Long ownerId) {
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
                        .setRole(
                            Objects.equals(user.getUserId(), ownerId)
                                ? TrackRole.OWNER
                                : TrackRole.FEATURE)
                        .setUserInfo(user))
            .toList();

    return trackUserRepository.saveAll(features);
  }
}
