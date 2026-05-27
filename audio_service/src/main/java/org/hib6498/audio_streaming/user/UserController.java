package org.hib6498.audio_streaming.user;

import java.util.List;

import org.hib6498.audio_streaming.shared.TracksForArtistDTO;
import org.hib6498.audio_streaming.track_user.TrackUserService;
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

  @GetMapping("/all")
  public ResponseEntity<List<UserInfoDTO>> getAllArtists() {
    List<UserInfoDTO> results = userService.getArtists();

    return ResponseEntity.ok(results);
  }

  @GetMapping("/{userId}/discography")
  public ResponseEntity<List<TracksForArtistDTO>> getUserDiscography(@PathVariable Long userId) {
    return ResponseEntity.ok(trackUserService.findTracksForUser(userId));
  }
}
