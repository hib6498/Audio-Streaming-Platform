package org.hib6498.audio_streaming.track;

import java.util.Optional;
import org.hib6498.audio_streaming.entities.TrackEntity;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.hib6498.audio_streaming.exceptions.NotFoundException;
import org.hib6498.audio_streaming.respositories.TrackRepository;
import org.springframework.stereotype.Service;

@Service
public class TrackService {
  private final TrackRepository trackRepository;
  private final StorageService storageService;

  public TrackService(TrackRepository repo, StorageService storageService) {
    this.trackRepository = repo;
    this.storageService = storageService;
  }

  public TrackEntity saveTrack(UserInfoEntity userInfoEntity, TrackUploadRequestDTO detailsDTO) {
    String trackSavedUri = storageService.saveTrack(detailsDTO.trackFile());

    String artworkSavedUri = "";
    if (detailsDTO.coverArt() != null) {
      artworkSavedUri = storageService.saveArtwork(detailsDTO.coverArt());
    }

    TrackEntity audioFile =
        new TrackEntity()
            .setTitle(detailsDTO.trackName())
            .setUserInfo(userInfoEntity)
            .setStorageLoc(trackSavedUri);

    if (!artworkSavedUri.isBlank()) {
      audioFile.setCoverArt(artworkSavedUri);
    }

    return trackRepository.save(audioFile);
  }

  public TrackEntity getTrack(Long trackId) {
    return trackRepository.findById(trackId).orElseThrow(() -> new NotFoundException("No tracks found."));
  }
}
