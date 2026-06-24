package org.hib6498.audio_streaming.respositories;

import org.hib6498.audio_streaming.entities.PlaylistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {}
