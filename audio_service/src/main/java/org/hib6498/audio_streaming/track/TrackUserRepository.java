package org.hib6498.audio_streaming.respositories;

import java.util.List;
import org.hib6498.audio_streaming.track.TrackUserEntity;
import org.hib6498.audio_streaming.track.TrackUserKey;
import org.hib6498.audio_streaming.shared.TrackArtistQueryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrackUserRepository extends JpaRepository<TrackUserEntity, TrackUserKey> {
  @Query(
      """
          SELECT new org.hib6498.audio_streaming.shared.TrackArtistQueryResult(
              t.trackId,
              u.userId,
              ta.role,
              u.displayName,
              t.createdAt,
              t.title
          )
          FROM TrackUserEntity ta
          JOIN ta.track t
          JOIN ta.userInfo u,
               TrackUserEntity taFilter
          WHERE ta.track = taFilter.track
            AND taFilter.userInfo.userId = :userId
          """)
  List<TrackArtistQueryResult> findTracksWithArtists(Long userId);

  @Query(
      """
          SELECT new org.hib6498.audio_streaming.shared.TrackArtistQueryResult(
              t.trackId,
              u.userId,
              ta.role,
              u.displayName,
              t.createdAt,
              t.title
          )
          FROM TrackUserEntity ta
          JOIN ta.track t
          JOIN ta.userInfo u
          ORDER BY t.createdAt DESC
        """)
  List<TrackArtistQueryResult> findAllTracksWithArtists();
}
