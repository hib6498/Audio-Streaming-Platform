package org.hib6498.audio_streaming.playlist;

import java.util.List;
import java.util.Optional;
import org.hib6498.audio_streaming.track.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {
  List<PlaylistEntity> findAllByUserInfo_UserId(Long UserId);

  @Query(
      "SELECT p FROM PlaylistEntity p LEFT JOIN FETCH p.tracks pt LEFT JOIN FETCH pt.track WHERE p.playlistId = :id")
  Optional<PlaylistEntity> findByIdWithTracks(@Param("id") Long id);
}
