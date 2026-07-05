package org.hib6498.audio_streaming.store;

import org.hib6498.audio_streaming.track.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageStatusRepository extends JpaRepository<StorageStatusEntity, Long> {
  Optional<StorageStatusEntity> findByTrackAndStatus(TrackEntity track, UploadStatus status);

  Optional<StorageStatusEntity> findByTrack_TrackId(Long trackId);
}
