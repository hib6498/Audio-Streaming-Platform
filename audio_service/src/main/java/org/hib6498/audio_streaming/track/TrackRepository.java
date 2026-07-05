package org.hib6498.audio_streaming.track;

import java.util.List;
import java.util.Optional;
import org.hib6498.audio_streaming.user.UserInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {
  @Query(
      """
                SELECT DISTINCT new org.hib6498.audio_streaming.user.UserInfoDTO(
                    u.userId,
                    u.username,
                    u.displayName,
                    u.profilePic
                )
                FROM TrackUserEntity tu
                JOIN tu.userInfo u
                JOIN FETCH TrackEntity t
            """)
  List<UserInfoDTO> getAllSongsForUser();

  @Query("SELECT t FROM TrackEntity t JOIN FETCH t.store WHERE t.trackId = :trackId")
  Optional<TrackEntity> findByIdWithStore(@Param("trackId") Long trackId);

  @Query(
      "SELECT t FROM TrackEntity t JOIN FETCH t.features JOIN FETCH t.store WHERE t.trackId = :trackId")
  Optional<TrackEntity> findByIdWithFeatures(@Param("trackId") Long trackId);
}
