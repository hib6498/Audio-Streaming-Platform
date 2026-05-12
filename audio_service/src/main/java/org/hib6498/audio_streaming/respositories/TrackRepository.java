package org.hib6498.audio_streaming.respositories;

import org.hib6498.audio_streaming.entities.TrackEntity;
import org.hib6498.audio_streaming.user.UserInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
            """)
    List<UserInfoDTO> getAllSongsForUser();
}
