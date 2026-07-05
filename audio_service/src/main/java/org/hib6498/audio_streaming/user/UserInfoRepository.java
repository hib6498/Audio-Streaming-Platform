package org.hib6498.audio_streaming.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
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
  List<UserInfoDTO> getAllArtists();
}
