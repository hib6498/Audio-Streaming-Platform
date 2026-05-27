package org.hib6498.audio_streaming.playlist_track;

import java.sql.Timestamp;
import java.util.List;

import org.hib6498.audio_streaming.shared.UserIdDisplayNameDTO;
import org.hib6498.audio_streaming.shared.TracksForArtistDTO;

public record PlaylistResponseDTO(
    Long id,
    String title,
    UserIdDisplayNameDTO userInfo,
    Timestamp createdAt,
    List<TracksForArtistDTO> tracks,
    String coverArt) {}
