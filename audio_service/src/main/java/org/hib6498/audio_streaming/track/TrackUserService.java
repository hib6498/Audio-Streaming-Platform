package org.hib6498.audio_streaming.track;

import static org.hib6498.audio_streaming.track.daos.TrackArtistQueryConverter.convertTAQueryResultToTFADTO;

import java.util.List;
import java.util.Objects;

import org.hib6498.audio_streaming.track.daos.TrackArtistQueryResult;
import org.hib6498.audio_streaming.track.dtos.TracksForArtist;
import org.hib6498.audio_streaming.user.UserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackUserService {
  @Autowired private TrackUserRepository trackUserRepository;

  public List<TracksForArtist> getAllTracks() {
    List<TrackArtistQueryResult> results = trackUserRepository.findAllTracksWithArtists();
    return convertTAQueryResultToTFADTO(results);
  }

  public List<TracksForArtist> findTracksForUser(Long userId) {
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
