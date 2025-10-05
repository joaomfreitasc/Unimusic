package com.project.unimusic.services;

import com.project.unimusic.entidades.Playlist;
import com.project.unimusic.entidades.Musica;
import com.project.unimusic.repositories.PlaylistRepository;
import com.project.unimusic.repositories.MusicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import java.util.Optional;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private MusicaRepository musicaRepository;

    public Playlist save(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Optional<Playlist> findById(UUID id) {
        return playlistRepository.findById(id);
    }

    @Transactional
    public Optional<Playlist> addMusicaToPlaylist(UUID playlistId, UUID musicaId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        Optional<Musica> musicaOpt = musicaRepository.findById(musicaId);

        if (playlistOpt.isPresent() && musicaOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Musica musica = musicaOpt.get();
            playlist.getMusicas().add(musica);
            return Optional.of(playlist);
        }

        return Optional.empty();
    }
}
