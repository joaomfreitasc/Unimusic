package com.project.unimusic.repositories;

import com.project.unimusic.entidades.Artista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistaRepository extends JpaRepository<Artista, java.util.UUID> {
}
