package org.hib6498.audio_streaming.playlist;

import java.util.List;
import java.util.Objects;
import org.hib6498.audio_streaming.playlist.dtos.PlaylistInfoResponse;
import org.hib6498.audio_streaming.playlist.dtos.PlaylistRequest;
import org.hib6498.audio_streaming.playlist.dtos.PlaylistResponse;
import org.hib6498.audio_streaming.store.StorageService;
import org.hib6498.audio_streaming.track.TrackEntity;
import org.hib6498.audio_streaming.track.TrackService;
import org.hib6498.audio_streaming.user.UserInfoEntity;
import org.hib6498.audio_streaming.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
  @Autowired private PlaylistService playlistService;
  @Autowired private TrackService trackService;
  @Autowired private StorageService storageService;
  @Autowired private UserService userService;

  @GetMapping("/all")
  public ResponseEntity<List<PlaylistInfoResponse>> getAllPlaylists() {
    List<PlaylistInfoResponse> playlists = playlistService.getAllPlaylists();
    return ResponseEntity.ok(playlists);
  }

  @GetMapping("/{playlistId}")
  public ResponseEntity<PlaylistResponse> getPlaylist(@PathVariable Long playlistId) {
    PlaylistEntity playlist = playlistService.getFullPlaylist(playlistId);

    return ResponseEntity.ok(PlaylistResponse.from(playlist));
  }

  @PostMapping("/new")
  public ResponseEntity<PlaylistInfoResponse> createNewPlaylist(
      @RequestBody PlaylistRequest playlistInfo, @AuthenticationPrincipal UserInfoEntity userInfo) {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);

    PlaylistEntity newPlaylist = playlistService.createNewPlaylist(playlistInfo, user);
    return ResponseEntity.ok(PlaylistInfoResponse.from(newPlaylist));
  }

  @PutMapping("/{playlistId}/tracks/{trackId}")
  public ResponseEntity<PlaylistResponse> addTrackToPlaylist(
      @PathVariable Long trackId,
      @PathVariable Long playlistId,
      @AuthenticationPrincipal UserInfoEntity userInfo) {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);
    TrackEntity track = trackService.getTrack(trackId);
    PlaylistEntity updatedPlaylist =
        playlistService.addTrackToPlaylist(track, playlistId, user.getUserId());

    return ResponseEntity.ok(PlaylistResponse.from(updatedPlaylist));
  }

  @GetMapping("/{playlistId}/artwork")
  public ResponseEntity<Resource> getTrackArtwork(@PathVariable Long playlistId) {
    PlaylistEntity details = playlistService.getPlaylist(playlistId);

    if (Objects.isNull(details.getCoverArt())) {
      Resource defaultArt = new ClassPathResource("static/playlist-nobg.png");
      return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultArt);
    }

    Resource trackResource = storageService.loadAsResource(details.getCoverArt());
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(trackResource);
  }

  @DeleteMapping("/{playlistId}/tracks/{trackId}")
  public ResponseEntity<PlaylistResponse> removeTrackFromPlaylist(
      @PathVariable Long trackId,
      @PathVariable Long playlistId,
      @AuthenticationPrincipal UserInfoEntity userInfo) {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);
    TrackEntity track = trackService.getTrack(trackId);
    PlaylistEntity updatedPlaylist =
        playlistService.removeTrackFromPlaylist(track, playlistId, user.getUserId());

    return ResponseEntity.ok(PlaylistResponse.from(updatedPlaylist));
  }
}
