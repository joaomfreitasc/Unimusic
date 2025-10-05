package com.project.unimusic.services;

import com.project.unimusic.entidades.Artista;
import com.project.unimusic.repositories.ArtistaRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistaService {

    @Autowired
    private ArtistaRepository artistaRepository;

    public Artista save(Artista artista) {
        return artistaRepository.save(artista);
    }

    public List<Artista> findAll() {
        return artistaRepository.findAll();
    }

    public Optional<Artista> findById(UUID id) {
        return artistaRepository.findById(id);
    }
}

