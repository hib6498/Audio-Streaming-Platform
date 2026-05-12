package org.hib6498.audio_streaming.track_user;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hib6498.audio_streaming.user.UserIdDisplayNameDTO;

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
