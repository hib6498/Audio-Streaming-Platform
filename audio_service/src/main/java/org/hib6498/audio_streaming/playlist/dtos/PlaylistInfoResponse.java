package org.hib6498.audio_streaming.playlist.dtos;

import org.hib6498.audio_streaming.playlist.PlaylistEntity;
import org.hib6498.audio_streaming.user.UserIdDisplayNameDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record PlaylistInfoResponse(
    Long id, String title, UserIdDisplayNameDTO userInfo, String coverArt) {

  public static PlaylistInfoResponse from(PlaylistEntity playlist) {
    UserIdDisplayNameDTO user =
        new UserIdDisplayNameDTO(
            playlist.getUserInfo().getUserId(), playlist.getUserInfo().getDisplayName());
    return new PlaylistInfoResponse(
        playlist.getPlaylistId(),
        playlist.getTitle(),
        user,
        craftArtworkUrl(playlist.getPlaylistId()));
  }

  private static String craftArtworkUrl(Long playlistId) {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/playlist/{id}/artwork")
        .buildAndExpand(playlistId)
        .toUriString();
  }
}
