package org.hib6498.audio_streaming.playlist.dtos;

import java.sql.Timestamp;
import java.util.List;
import org.hib6498.audio_streaming.playlist.PlaylistEntity;
import org.hib6498.audio_streaming.track.dtos.TrackInfo;
import org.hib6498.audio_streaming.user.UserIdDisplayNameDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record PlaylistResponse(
    Long id,
    String title,
    UserIdDisplayNameDTO userInfo,
    Timestamp createdAt,
    List<TrackInfo> tracks,
    String coverArt) {

  public static PlaylistResponse from(PlaylistEntity playlist) {
    UserIdDisplayNameDTO user =
        new UserIdDisplayNameDTO(
            playlist.getUserInfo().getUserId(), playlist.getUserInfo().getDisplayName());

    List<TrackInfo> tracks =
        playlist.getTracks().stream().map(track -> TrackInfo.from(track.getTrack())).toList();
    return new PlaylistResponse(
        playlist.getPlaylistId(),
        playlist.getTitle(),
        user,
        playlist.getCreatedAt(),
        tracks,
        craftArtworkUrl(playlist.getPlaylistId()));
  }

  private static String craftArtworkUrl(Long playlistId) {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/playlist/{id}/artwork")
        .buildAndExpand(playlistId)
        .toUriString();
  }
}
