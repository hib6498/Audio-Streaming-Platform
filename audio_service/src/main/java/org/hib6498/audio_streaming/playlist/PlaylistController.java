package org.hib6498.audio_streaming.playlist;

import org.hib6498.audio_streaming.entities.PlaylistEntity;
import org.hib6498.audio_streaming.entities.TrackEntity;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.hib6498.audio_streaming.playlist_track.PlaylistResponseDTO;
import org.hib6498.audio_streaming.playlist_track.PlaylistTrackService;
import org.hib6498.audio_streaming.track.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
  @Autowired private PlaylistService playlistService;
  @Autowired private TrackService trackService;
  @Autowired private PlaylistTrackService playlistTrackService;

  @GetMapping("/all")
  public ResponseEntity<List<PlaylistInfoResponseDTO>> getAllPlaylists() {
    List<PlaylistInfoResponseDTO> playlists = playlistService.getAllPlaylists();
    return ResponseEntity.ok(playlists);
  }

  @GetMapping("/{playlistId}")
  public ResponseEntity<PlaylistResponseDTO> getPlaylist(@PathVariable Long playlistId) {
    PlaylistEntity playlist = playlistService.getPlaylist(playlistId);
    PlaylistResponseDTO response = playlistTrackService.getPlaylistInfo(playlist);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/new")
  public ResponseEntity<PlaylistEntity> createNewPlaylist(
      @RequestBody PlaylistRequest playlistInfo, @AuthenticationPrincipal UserInfoEntity user) {
    return ResponseEntity.ok(playlistService.createNewPlaylist(playlistInfo, user));
  }

  @PutMapping("/{playlistId}/tracks/{trackId}")
  public ResponseEntity<PlaylistEntity> addTrackToPlaylist(
      @PathVariable Long trackId,
      @PathVariable Long playlistId,
      @AuthenticationPrincipal UserInfoEntity user) {
    TrackEntity track = trackService.getTrack(trackId);
    PlaylistEntity updatedPlaylist =
        playlistService.addTrackToPlaylist(track, playlistId, user.getUserId());

    return ResponseEntity.ok(updatedPlaylist);
  }

  @DeleteMapping("/{playlistId}/tracks/{trackId}")
  public ResponseEntity<PlaylistEntity> removeTrackFromPlaylist(
      @PathVariable Long trackId,
      @PathVariable Long playlistId,
      @AuthenticationPrincipal UserInfoEntity user) {
    TrackEntity track = trackService.getTrack(trackId);
    PlaylistEntity updatedPlaylist =
        playlistService.removeTrackFromPlaylist(track, playlistId, user.getUserId());

    return ResponseEntity.ok(updatedPlaylist);
  }
}
