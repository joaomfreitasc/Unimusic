package com.project.unimusic.services;

import com.project.unimusic.entidades.Musica;
import com.project.unimusic.repositories.MusicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MusicaService {

    @Autowired
    private MusicaRepository musicaRepository;

    public List<Musica> findAll() {
        return musicaRepository.findAll();
    }

    public Optional<Musica> findById(UUID id) {
        return musicaRepository.findById(id);
    }
    
    public Musica save(Musica musica) {
        List<Musica> existing = musicaRepository.findByTituloContainingIgnoreCase(musica.getTitulo());
        for (Musica m : existing) {
            if (m.getTitulo().equalsIgnoreCase(musica.getTitulo())) {
                return m;
            }
        }
        return musicaRepository.save(musica);
    }

    public List<Musica> searchByTitulo(String titulo) {
        return musicaRepository.findByTituloContainingIgnoreCase(titulo);
    }

}


