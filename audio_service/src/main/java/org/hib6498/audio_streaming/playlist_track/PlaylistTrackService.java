package org.hib6498.audio_streaming.playlist_track;

import java.util.List;
import org.hib6498.audio_streaming.entities.PlaylistEntity;
import org.hib6498.audio_streaming.respositories.PlaylistTrackRepository;
import org.hib6498.audio_streaming.shared.TrackArtistQueryConverter;
import org.hib6498.audio_streaming.shared.TrackArtistQueryResult;
import org.hib6498.audio_streaming.shared.UserIdDisplayNameDTO;
import org.hib6498.audio_streaming.shared.TracksForArtistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaylistTrackService {
  @Autowired private PlaylistTrackRepository playlistTrackRepository;

  public PlaylistResponseDTO getPlaylistInfo(PlaylistEntity playlist) {
    List<TrackArtistQueryResult> results =
        playlistTrackRepository.findPlaylistTrackInfo(playlist.getPlaylistId());
    List<TracksForArtistDTO> trackInfo =
        TrackArtistQueryConverter.convertTAQueryResultToTFADTO(results);
    UserIdDisplayNameDTO userInfo =
        new UserIdDisplayNameDTO(
            playlist.getUserInfo().getUserId(), playlist.getUserInfo().getDisplayName());

    return new PlaylistResponseDTO(
        playlist.getPlaylistId(),
        playlist.getTitle(),
        userInfo,
        playlist.getCreatedAt(),
        trackInfo,
        "");
  }
}
