package org.hib6498.audio_streaming.playlist;

import org.hib6498.audio_streaming.shared.UserIdDisplayNameDTO;

public record PlaylistInfoResponseDTO(Long id, String title, UserIdDisplayNameDTO userInfo, String coverArt) {}
