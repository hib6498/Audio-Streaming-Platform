package org.hib6498.audio_streaming.shared;

import java.sql.Timestamp;

public record TrackArtistQueryResult(
    Long trackId,
    Long userId,
    boolean isFeature,
    String displayName,
    Timestamp createdAt,
    String title) {}
;
