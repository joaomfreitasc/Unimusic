package com.project.unimusic.services;

import com.project.unimusic.entidades.Album;
import com.project.unimusic.repositories.AlbumRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    public Optional<Album> findById(UUID id) {
        return albumRepository.findById(id);
    }

    public Album save(Album album) {
        return albumRepository.save(album);
    }

    public Optional<Album> update(UUID id, Album albumDetails) {
        Optional<Album> albumOpt = albumRepository.findById(id);

        if (albumOpt.isPresent()) {
            Album albumExistente = albumOpt.get();
            albumExistente.setTitulo(albumDetails.getTitulo());
            return Optional.of(albumRepository.save(albumExistente));
        }

        return Optional.empty();
    }

    public boolean deleteById(UUID id) {
        if (!albumRepository.existsById(id)) {
            return false;
        }
        albumRepository.deleteById(id);
        return true;
    }
}
