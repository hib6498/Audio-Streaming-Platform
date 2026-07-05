package org.hib6498.audio_streaming.track;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hib6498.audio_streaming.exceptions.ForbiddenException;
import org.hib6498.audio_streaming.exceptions.NotFoundException;
import org.hib6498.audio_streaming.playlist.PlaylistService;
import org.hib6498.audio_streaming.store.S3StorageService;
import org.hib6498.audio_streaming.store.StorageService;
import org.hib6498.audio_streaming.track.dtos.UpdateTrackMetadataRequest;
import org.hib6498.audio_streaming.user.UserInfoEntity;
import org.hib6498.audio_streaming.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrackService {
  private final TrackRepository trackRepository;
  private final StorageService storageService;
  private final UserService userService;
  private final S3StorageService s3StorageService;
  private final PlaylistService playlistService;

  public TrackService(
      TrackRepository repo,
      StorageService storageService,
      UserService userService,
      S3StorageService s3StorageService,
      PlaylistService playlistService) {
    this.trackRepository = repo;
    this.storageService = storageService;
    this.userService = userService;
    this.s3StorageService = s3StorageService;
    this.playlistService = playlistService;
  }

  @Transactional
  public TrackEntity saveTrackMetadata(
      UserInfoEntity userInfoEntity, UpdateTrackMetadataRequest detailsDTO, Long trackId) {
    String artworkSavedUri = "";
    if (detailsDTO.coverArt() != null) {
      artworkSavedUri = storageService.saveArtwork(detailsDTO.coverArt());
    }

    TrackEntity track =
        trackRepository
            .findByIdWithFeatures(trackId)
            .orElseThrow(() -> new NotFoundException("Track doesn't exist for given is"));

    if (detailsDTO.featureIds().isPresent()) {
      List<UserInfoEntity> features = new ArrayList<>();
      features.add(userInfoEntity);
      features.addAll(userService.gatherUsersInfo(detailsDTO.featureIds().get().stream().toList()));
      updateFeaturesForTrack(track, features, userInfoEntity.getUserId());
    }

    boolean isOwner =
        Objects.equals(
            track.getFeatures().stream()
                .filter(entity -> entity.getRole() == TrackRole.OWNER)
                .findAny()
                .orElseThrow(() -> new RuntimeException("No owner found"))
                .getUserInfo()
                .getUserId(),
            userInfoEntity.getUserId());
    if (!isOwner) throw new ForbiddenException("You do not own the track");
    track.setTitle(detailsDTO.trackName());

    if (!artworkSavedUri.isBlank()) {
      track.setCoverArt(artworkSavedUri);
    }

    return track;
  }

  public TrackEntity initializeTrackInfo(String title) {
    TrackEntity trackEntity = new TrackEntity().setTrackStatus(TrackStatus.DRAFT).setTitle(title);
    return trackRepository.save(trackEntity);
  }

  @Transactional
  public TrackEntity markCompleteTrackUpload(Long trackId, String s3Key) {
    TrackEntity trackEntity =
        trackRepository
            .findByIdWithStore(trackId)
            .orElseThrow(() -> new NotFoundException("No track entry found."));
    trackEntity.setTrackStatus(TrackStatus.READY);
    trackEntity.getStore().setS3Key(s3Key);
    return trackRepository.save(trackEntity);
  }

  public TrackEntity markTrackEncoding(Long trackId) {
    TrackEntity trackEntity =
        trackRepository
            .findById(trackId)
            .orElseThrow(() -> new NotFoundException("No track entry found."));
    trackEntity.setTrackStatus(TrackStatus.ENCODING);
    return trackRepository.save(trackEntity);
  }

  public TrackEntity getTrack(Long trackId) {
    return trackRepository
        .findById(trackId)
        .orElseThrow(() -> new NotFoundException("No tracks found."));
  }

  public TrackEntity getTrackWithStore(Long trackId) {
    return trackRepository
        .findByIdWithStore(trackId)
        .orElseThrow(() -> new NotFoundException("No tracks found."));
  }

  @Transactional
  public void deleteTrack(Long trackId, UserInfoEntity user) {
    TrackEntity track =
        trackRepository
            .findByIdWithFeatures(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found."));

    UserInfoEntity owner =
        track.getFeatures().stream()
            .filter(trackUserEntity -> trackUserEntity.getRole() == TrackRole.OWNER)
            .findAny()
            .orElseThrow(
                () -> new RuntimeException("You fckd up the logic and now there is no owner"))
            .getUserInfo();
    if (!Objects.equals(owner.getUserId(), user.getUserId()))
      throw new ForbiddenException("You do not own this track");

    s3StorageService.deleteObject(track.getStore().getS3Key());
    playlistService.deleteTrackInstances(track);
    trackRepository.delete(track);
  }

  @Transactional
  public List<TrackUserEntity> updateFeaturesForTrack(
      TrackEntity track, List<UserInfoEntity> featuresInfo, Long ownerId) {

    track.getFeatures().clear();
    trackRepository.flush(); // gotta force this or it will raise NonUniqueObjectException

    List<TrackUserEntity> features =
        featuresInfo.stream()
            .map(
                user ->
                    new TrackUserEntity()
                        .setKey(
                            new TrackUserKey()
                                .setTrackId(track.getTrackId())
                                .setUserId(user.getUserId()))
                        .setTrack(track)
                        .setRole(
                            Objects.equals(user.getUserId(), ownerId)
                                ? TrackRole.OWNER
                                : TrackRole.FEATURE)
                        .setUserInfo(user))
            .toList();

    track.getFeatures().addAll(features);
    return track.getFeatures();
  }
}
