package com.project.unimusic.services;

import com.project.unimusic.entidades.Musica;
import com.project.unimusic.repositories.MusicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.File;

@Service
public class MusicaService {

    @Autowired
    private MusicaRepository musicaRepository;

    @Autowired
    private S3FileService s3FileService;

    @Value("${aws.s3_bucket_name}")
    private String srBucketName;

    public List<Musica> findAll() {
        return musicaRepository.findAll();
    }

    public Optional<Musica> findById(UUID id) {
        return musicaRepository.findById(id);
    }

    public Musica save(Musica musica) {
        return musicaRepository.save(musica);
    }

    public Optional<Musica> update(UUID id, Musica musicaDetails) {
        Optional<Musica> musicaOpt = musicaRepository.findById(id);

        if (musicaOpt.isPresent()) {
            Musica musicaExistente = musicaOpt.get();
            musicaExistente.setTitulo(musicaDetails.getTitulo());
            return Optional.of(musicaRepository.save(musicaExistente));
        }

        return Optional.empty();
    }

    public List<Musica> searchByTitulo(String titulo) {
        return musicaRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public boolean deleteById(UUID id) {
        if (!musicaRepository.existsById(id)) {
            return false;
        }
        musicaRepository.deleteById(id);
        return true;
    }

    public InputStreamResource streamFile(String key) {
        return s3FileService.streamFile(srBucketName, key);
    }   
}
