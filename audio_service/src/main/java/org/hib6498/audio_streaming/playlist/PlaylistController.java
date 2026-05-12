package org.hib6498.audio_streaming.playlist;

import org.hib6498.audio_streaming.entities.PlaylistEntity;
import org.hib6498.audio_streaming.entities.TrackEntity;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.hib6498.audio_streaming.track.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
  @Autowired private PlaylistService playlistService;
  @Autowired private TrackService trackService;

  @GetMapping()
  public ResponseEntity<?> getPlaylist() {
    return ResponseEntity.ok().build();
  }

  @PostMapping("/new")
  public ResponseEntity<PlaylistEntity> createNewPlaylist(
      @RequestBody PlaylistRequest playlistInfo) {
    return ResponseEntity.ok(playlistService.createNewPlaylist(playlistInfo));
  }

  @PutMapping("/{playlistId}/add")
  public ResponseEntity<PlaylistEntity> addToPlaylist(
      @RequestBody AddToPlaylistRequest addRequest,
      @PathVariable Long playlistId,
      @AuthenticationPrincipal UserInfoEntity user) {
    TrackEntity track = trackService.getTrack(addRequest.trackId());
    PlaylistEntity updatedPlaylist = playlistService.addTrackToPlaylist(track, playlistId, user.getUserId());

    return ResponseEntity.ok(updatedPlaylist);
  }
}
