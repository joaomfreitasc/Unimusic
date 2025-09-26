package br.unibh.sdm.unimusic_music.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.unibh.sdm.unimusic_music.entidades.Musica;
import br.unibh.sdm.unimusic_music.negocio.MusicaService;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "music")
public class MusicaController {
    
    private final MusicaService musicService;

    public MusicaController(MusicaService musicService) {
        this.musicService = musicService;
    }

    @GetMapping
    public List<Musica> getMusics(@RequestParam(required = false) String titulo) {
        if(titulo != null && !titulo.isEmpty()) {
            return musicService.getMusicByTitulo(titulo);
        }
        return musicService.getAllMusics();
    }
    @GetMapping("/{id}")
    public Musica getMusicById(@PathVariable String id) {
        return musicService.getMusicById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="", consumes=MediaType.APPLICATION_JSON_VALUE)
    public Musica createMusic(@RequestBody @NotNull Musica music) {
        return musicService.createMusic(music);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public boolean deleteMusic(@PathVariable String id) {
        musicService.deleteMusic(id);
        return true;
    }
}
