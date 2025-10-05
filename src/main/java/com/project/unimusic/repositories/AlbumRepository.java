package com.project.unimusic.repositories;

import com.project.unimusic.entidades.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, java.util.UUID> {
}

