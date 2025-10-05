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

    public Album save(Album album) {
        return albumRepository.save(album);
    }

    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    public Optional<Album> findById(UUID id) {
        return albumRepository.findById(id);
    }
}
