package com.project.unimusic.repositories;

import com.project.unimusic.entidades.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, java.util.UUID> {
}


