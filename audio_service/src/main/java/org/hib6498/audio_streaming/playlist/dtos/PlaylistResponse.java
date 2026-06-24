package org.hib6498.audio_streaming.playlist;

import java.sql.Timestamp;
import java.util.List;

import org.hib6498.audio_streaming.user.UserIdDisplayNameDTO;
import org.hib6498.audio_streaming.track.dtos.TracksForArtist;

public record PlaylistResponseDTO(
    Long id,
    String title,
    UserIdDisplayNameDTO userInfo,
    Timestamp createdAt,
    List<TracksForArtist> tracks,
    String coverArt) {}
