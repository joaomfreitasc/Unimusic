package com.project.unimusic.repositories;

import com.project.unimusic.entidades.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MusicaRepository extends JpaRepository<Musica, java.util.UUID> {
    List<Musica> findByTituloContainingIgnoreCase(String titulo);
}