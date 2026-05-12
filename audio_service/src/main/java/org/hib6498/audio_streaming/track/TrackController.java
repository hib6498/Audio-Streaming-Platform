package org.hib6498.audio_streaming.track;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hib6498.audio_streaming.entities.TrackEntity;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.hib6498.audio_streaming.exceptions.NotFoundException;
import org.hib6498.audio_streaming.track_user.TrackUserService;
import org.hib6498.audio_streaming.user.TracksForArtistDTO;
import org.hib6498.audio_streaming.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/track")
public class TrackController {
  @Autowired private TrackService trackService;
  @Autowired private StorageService storageService;
  @Autowired private UserService userService;
  @Autowired private TrackUserService trackUserService;

  @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<TrackEntity> fileUpload(
      HttpServletRequest request,
      @ModelAttribute TrackUploadRequestDTO detailsDTO,
      @AuthenticationPrincipal UserInfoEntity userInfo)
      throws IOException {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);
    List<UserInfoEntity> features = new ArrayList<>();
    if (detailsDTO.featureIds().isPresent()) {
      features = userService.gatherUsersInfo(detailsDTO.featureIds().get().stream().toList());
    }

    TrackEntity savedTrack = trackService.saveTrack(user, detailsDTO);
    features.add(user);

    trackUserService.saveFeaturesForTrack(savedTrack, features, user.getUserId());
    return ResponseEntity.ok(savedTrack);
  }

  @GetMapping("/{trackId}")
  public ResponseEntity<Resource> getFile(@PathVariable Long trackId) {
    TrackEntity details = trackService.getTrack(trackId);

    Resource trackResource = storageService.loadAsResource(details.getStorageLoc());

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("audio/mpeg"))
        .body(trackResource);
  }

  @GetMapping("/{trackId}/artwork")
  public ResponseEntity<Resource> getTrackArtwork(@PathVariable Long trackId) {
    TrackEntity details = trackService.getTrack(trackId);
    if (Objects.isNull(details.getCoverArt())) {
      throw new NotFoundException("No artwork available for this track.");
    }

    Resource trackResource = storageService.loadAsResource(details.getCoverArt());

    return ResponseEntity.ok()
        .contentType(MediaType.valueOf(MediaType.IMAGE_JPEG_VALUE))
        .body(trackResource);
  }

  @GetMapping("/all")
  public ResponseEntity<List<TracksForArtistDTO>> getAllTracks() {
    return ResponseEntity.ok(trackUserService.getAllTracks());
  }
}
