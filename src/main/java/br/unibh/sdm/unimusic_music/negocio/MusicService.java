package br.unibh.sdm.unimusic_music.negocio;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.unibh.sdm.unimusic_music.persistencia.MusicRepository;
import br.unibh.sdm.unimusic_music.entidades.Music;
import br.unibh.sdm.unimusic_music.exceptions.NotFoundException;

@Service
public class MusicService {

    private static Logger LOGGER = LoggerFactory.getLogger(MusicService.class);
    
    private final MusicRepository musicRepository;

    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Music> getAllMusics() {
        Iterable<Music> allMusics = musicRepository.findAll();
        return StreamSupport.stream(allMusics.spliterator(), false).collect(Collectors.toList());
    }

    public Music getMusicById(String id) {
        return musicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Música com id " + id + " não encontrada."));
    }

    public List<Music> getMusicByTitulo(String titulo) {
        List<Music> music = musicRepository.findByTitulo(titulo);
        if(!music.isEmpty()) {
            return music;
        }
        throw new NotFoundException("A música com o titulo " + titulo + " não foi encontrada.");
    }

    public Music createMusic(Music music) {
        return musicRepository.save(music);
    }

    public void deleteMusic(String id) {
        if(!musicRepository.existsById(id)) {
            throw new NotFoundException("Música com id " + id + " não encontrada.");
        }
        musicRepository.deleteById(id);
    }
}
