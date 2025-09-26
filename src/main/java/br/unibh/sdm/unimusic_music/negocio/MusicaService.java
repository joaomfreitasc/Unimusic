package br.unibh.sdm.unimusic_music.negocio;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.unibh.sdm.unimusic_music.persistencia.MusicaRepository;
import br.unibh.sdm.unimusic_music.entidades.Musica;
import br.unibh.sdm.unimusic_music.exceptions.NotFoundException;

@Service
public class MusicaService {

    private static Logger LOGGER = LoggerFactory.getLogger(MusicaService.class);
    
    private final MusicaRepository musicRepository;

    public MusicaService(MusicaRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Musica> getAllMusics() {
        Iterable<Musica> allMusics = musicRepository.findAll();
        return StreamSupport.stream(allMusics.spliterator(), false).collect(Collectors.toList());
    }

    public Musica getMusicById(String id) {
        return musicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Música com id " + id + " não encontrada."));
    }

    public List<Musica> getMusicByTitulo(String titulo) {
        List<Musica> music = musicRepository.findByTitulo(titulo);
        if(!music.isEmpty()) {
            return music;
        }
        throw new NotFoundException("A música com o titulo " + titulo + " não foi encontrada.");
    }

    public Musica createMusic(Musica music) {
        return musicRepository.save(music);
    }

    public void deleteMusic(String id) {
        if(!musicRepository.existsById(id)) {
            throw new NotFoundException("Música com id " + id + " não encontrada.");
        }
        musicRepository.deleteById(id);
    }
}
