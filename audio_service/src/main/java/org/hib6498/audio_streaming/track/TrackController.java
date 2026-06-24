package org.hib6498.audio_streaming.track;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import org.hib6498.audio_streaming.exceptions.ForbiddenException;
import org.hib6498.audio_streaming.store.S3StorageService;
import org.hib6498.audio_streaming.store.StorageService;
import org.hib6498.audio_streaming.store.StorageStatusEntity;
import org.hib6498.audio_streaming.track.dtos.*;
import org.hib6498.audio_streaming.user.UserInfoEntity;
import org.hib6498.audio_streaming.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/track")
public class TrackController {
  @Autowired private TrackService trackService;
  @Autowired private StorageService storageService;
  @Autowired private UserService userService;
  @Autowired private TrackUserService trackUserService;
  @Autowired private S3StorageService s3StorageService;
  @Autowired private TrackEventProducer trackEventProducer;

  @GetMapping("/{trackId}")
  public ResponseEntity<TrackInfo> getTrack(@PathVariable Long trackId) {
    TrackEntity details = trackService.getTrack(trackId);
    if (details.getTrackStatus() != TrackStatus.READY)
      throw new ForbiddenException("Track is not yet ready");

    TrackInfo response = TrackInfo.from(details);
    return ResponseEntity.ok(response);
  }

  @GetMapping("{trackId}/stream-url")
  public ResponseEntity<String> getTrackStreamUrl(@PathVariable Long trackId) {
    TrackEntity track = trackService.getTrackWithStore(trackId);
    URL s3Url = s3StorageService.generatePresignedGetUrlForTrack(track);
    return ResponseEntity.ok(s3Url.toString());
  }

  @GetMapping("/{trackId}/artwork")
  public ResponseEntity<Resource> getTrackArtwork(@PathVariable Long trackId) {
    TrackEntity details = trackService.getTrack(trackId);

    if (Objects.isNull(details.getCoverArt())) {
      Resource defaultArt = new ClassPathResource("static/music-note-nobg.png");
      return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultArt);
    }

    Resource trackResource = storageService.loadAsResource(details.getCoverArt());
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(trackResource);
  }

  @GetMapping("/all")
  public ResponseEntity<List<TracksForArtist>> getAllTracks() {
    return ResponseEntity.ok(trackUserService.getAllTracks());
  }

  @PutMapping(path = "/{trackId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<TrackInfo> updateTrackMetadata(
      @PathVariable Long trackId,
      @ModelAttribute UpdateTrackMetadataRequest detailsDTO,
      @AuthenticationPrincipal UserInfoEntity userInfo) {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);

    TrackEntity trackEntity = trackService.saveTrackMetadata(user, detailsDTO, trackId);
    return ResponseEntity.ok(TrackInfo.from(trackEntity));
  }

  @DeleteMapping(value = "/{trackId}")
  public ResponseEntity<Void> deleteTrack(
      @PathVariable Long trackId, @AuthenticationPrincipal UserInfoEntity userInfo) {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);
    trackService.deleteTrack(trackId, user);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/upload/presigned-url")
  public ResponseEntity<TrackPresignedUploadResponse> generatePresignedUrlForTracks(
      @RequestBody TrackPresignedUploadRequest trackName,
      @AuthenticationPrincipal UserInfoEntity userInfo) {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);
    TrackEntity newTrack = trackService.initializeTrackInfo(trackName.trackName());
    List<TrackUserEntity> trackUserEntity =
        trackUserService.saveFeaturesForTrack(newTrack, List.of(userInfo), userInfo.getUserId());
    Pair<StorageStatusEntity, URL> storagePersist =
        s3StorageService.generatePresignedUrlForTrackUpload(newTrack);

    TrackPresignedUploadResponse response =
        new TrackPresignedUploadResponse(
            newTrack.getTrackId(),
            storagePersist.getFirst().getId(),
            storagePersist.getSecond().toString());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/upload/complete")
  @Transactional
  public ResponseEntity<Void> completeTrackUpload(
      @RequestBody TrackUploadCompleteRequest request,
      @AuthenticationPrincipal UserInfoEntity userInfo) {
    UserInfoEntity user = userService.populateUserInfoObject(userInfo);
    StorageStatusEntity statusEntity =
        s3StorageService.presignedPutRequestComplete(request.uploadId());
    TrackEntity trackEntity = trackService.markTrackEncoding(request.trackId());
    trackEventProducer.sendTrackProcessEvent(
        new TrackProcessEvent(trackEntity.getTrackId(), statusEntity.getS3Key()));
    return ResponseEntity.ok().build();
  }
}
