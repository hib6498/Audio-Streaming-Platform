package org.hib6498.audio_streaming.shared;

import java.sql.Timestamp;
import org.hib6498.audio_streaming.entities.TrackRole;

public record TrackArtistQueryResult(
    Long trackId,
    Long userId,
    TrackRole role,
    String displayName,
    Timestamp createdAt,
    String title) {}
;
