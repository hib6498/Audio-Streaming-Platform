package org.hib6498.audio_streaming.shared;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TrackInfo {
    private String title;
    private Timestamp createdAt;
    private UserIdDisplayNameDTO owns;
    private List<UserIdDisplayNameDTO> features;
    private String artworkUrl;
    private String audioUrl;
}
