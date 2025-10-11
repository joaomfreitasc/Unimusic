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

    public Optional<Artista> update(UUID id, Artista artistaDetails) {
        Optional<Artista> artistaOpt = artistaRepository.findById(id);
        
        if (artistaOpt.isPresent()) {
            Artista artistaExistente = artistaOpt.get();
            artistaExistente.setNome(artistaDetails.getNome());
            return Optional.of(artistaRepository.save(artistaExistente));
        }
        
        return Optional.empty();
    }

    public boolean deleteById(UUID id) {
        if (!artistaRepository.existsById(id)) {
            return false;
        }
        artistaRepository.deleteById(id);
        return true;
    }
}