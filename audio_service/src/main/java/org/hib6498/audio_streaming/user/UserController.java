package org.hib6498.audio_streaming.user;

import java.util.List;
import org.hib6498.audio_streaming.playlist.PlaylistEntity;
import org.hib6498.audio_streaming.playlist.PlaylistService;
import org.hib6498.audio_streaming.playlist.dtos.PlaylistInfoResponse;
import org.hib6498.audio_streaming.track.TrackUserService;
import org.hib6498.audio_streaming.track.dtos.TracksForArtist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired private UserService userService;
  @Autowired private TrackUserService trackUserService;
  @Autowired private PlaylistService playlistService;

  @GetMapping("/all")
  public ResponseEntity<List<UserInfoDTO>> getAllArtists() {
    List<UserInfoDTO> results = userService.getArtists();

    return ResponseEntity.ok(results);
  }

  @GetMapping("/{userId}/discography")
  public ResponseEntity<List<TracksForArtist>> getUserDiscography(@PathVariable Long userId) {
    return ResponseEntity.ok(trackUserService.findTracksForUser(userId));
  }

  @GetMapping("/{userId}/playlists")
  public ResponseEntity<List<PlaylistInfoResponse>> getUserPlaylists(@PathVariable Long userId) {
    List<PlaylistInfoResponse> playlists = playlistService.getAllPlaylistsOfUser(userId);
    return ResponseEntity.ok(playlists);
  }
}
