package com.project.unimusic.controllers;

import com.project.unimusic.services.MusicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.unimusic.entidades.Artista;
import com.project.unimusic.entidades.Album;
import com.project.unimusic.services.ArtistaService;
import com.project.unimusic.services.AlbumService;

import java.util.List;
import java.util.Optional;

import com.project.unimusic.dto.MusicaDTO;
import com.project.unimusic.dto.MusicaResponseDTO;
import com.project.unimusic.dto.MusicaRegisterDTO;
import java.util.stream.Collectors;
import java.util.UUID;

import com.project.unimusic.entidades.Musica;
import org.springframework.beans.BeanUtils;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    @Autowired
    private MusicaService musicaService;

    @Autowired
    private ArtistaService artistaService;

    @Autowired
    private AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<MusicaResponseDTO>> getAllMusicas() {
        List<MusicaResponseDTO> lista = musicaService.findAll()
                .stream()
                .map(MusicaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicaResponseDTO> getMusicaById(@PathVariable UUID id) {
        return musicaService.findById(id)
                .map(m -> ResponseEntity.ok(new MusicaResponseDTO(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> registrarMusica(@RequestBody @Valid MusicaRegisterDTO musicaDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        Optional<Artista> artistaOpt = artistaService.findById(musicaDTO.getArtistaId());

        if (artistaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artista não encontrado com o ID fornecido.");
        }

        Optional<Album> albumOpt = albumService.findById(musicaDTO.getAlbumId());

        if (albumOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Album não encontrado com o ID fornecido.");
        }

        Musica musica = new Musica();
        BeanUtils.copyProperties(musicaDTO, musica);

        musica.setArtista(artistaOpt.get());
        musica.setAlbum(albumOpt.get());

        Musica musicaSalva = musicaService.save(musica);
        return ResponseEntity.status(HttpStatus.CREATED).body(musicaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMusica(@PathVariable UUID id, @RequestBody @Valid MusicaDTO musicaDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }
        Optional<Musica> musicaOpt = musicaService.findById(id);
        if (musicaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Musica não encontrada.");
        }

        Musica musicaDetails = musicaOpt.get();
        musicaDetails.setTitulo(musicaDTO.getTitulo());
        musicaDetails.setDuracao(musicaDTO.getDuracao());

        Optional<Musica> updateMusicaOpt = musicaService.update(id, musicaDetails);
        if (updateMusicaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Musica não encontrada.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(updateMusicaOpt.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMusica(@PathVariable UUID id) {
        Optional<Musica> musicaOpt = musicaService.findById(id);
        if (musicaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Musica não encontrada.");
        }
        musicaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/stream/{artista}/{album}/{musica}")
    public ResponseEntity<Resource> streamMusica(@PathVariable String artista, @PathVariable String album,
            @PathVariable String musica) {
        try {
            String key = artista + "/" + album + "/" + musica + ".mp3";

            InputStreamResource resource = musicaService.streamFile(key);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + musica + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/capa/{artista}/{album}/{musica}")
    public ResponseEntity<InputStreamResource> streamCover(
            @PathVariable String artista,
            @PathVariable String album,
            @PathVariable String musica) {
        try {
            String key = artista + "/" + album + "/cover.jpg";
            InputStreamResource cover = musicaService.streamFile(key);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + musica + ".jpg\"")
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(cover);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}