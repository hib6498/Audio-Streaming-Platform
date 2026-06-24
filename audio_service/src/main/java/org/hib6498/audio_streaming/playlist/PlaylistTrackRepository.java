package org.hib6498.audio_streaming.respositories;

import java.util.List;
import org.hib6498.audio_streaming.entities.PlaylistTrackEntity;
import org.hib6498.audio_streaming.entities.PlaylistTrackKey;
import org.hib6498.audio_streaming.shared.TrackArtistQueryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaylistTrackRepository
    extends JpaRepository<PlaylistTrackEntity, PlaylistTrackKey> {
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
            FROM PlaylistTrackEntity pt
            JOIN pt.track t
            JOIN TrackUserEntity ta ON ta.track = t
            JOIN ta.userInfo u
            WHERE pt.playlist.playlistId = :playlistId
        """)
  List<TrackArtistQueryResult> findPlaylistTrackInfo(Long playlistId);
}
