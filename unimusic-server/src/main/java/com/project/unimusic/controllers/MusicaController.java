package com.project.unimusic.controllers;

import com.project.unimusic.services.MusicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.util.List;
import java.util.Optional;

import com.project.unimusic.dto.ArtistaDTO;
import com.project.unimusic.dto.AlbumDTO;
import com.project.unimusic.dto.MusicaDTO;
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

    @Value("${base}")
    private String base;

    @GetMapping
    public ResponseEntity<List<MusicaDTO>> getAllMusicas() {
        List<MusicaDTO> musicasDTO = musicaService.findAll().stream().map(musica -> {
            MusicaDTO musicaDTO = new MusicaDTO();
            musicaDTO.setId(musica.getId());
            musicaDTO.setTitulo(musica.getTitulo());
            musicaDTO.setDuracao(musica.getDuracao());

            ArtistaDTO artistaDTO = new ArtistaDTO();
            artistaDTO.setId(musica.getArtista().getId());
            artistaDTO.setNome(musica.getArtista().getNome());
            artistaDTO.setAlbums(musica.getArtista().getAlbums().stream().map(album -> {
                AlbumDTO albumDTO = new AlbumDTO();
                albumDTO.setId(album.getId());
                albumDTO.setTitulo(album.getTitulo());
                albumDTO.setDataDeLancamento(album.getDataDeLancamento());
                albumDTO.setCapaUrl(album.getCapaUrl());
                return albumDTO;
            }).collect(Collectors.toList()));

            musicaDTO.setArtista(artistaDTO);
            return musicaDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(musicasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMusicaById(@PathVariable UUID id) {
        Optional<Musica> musicaOpt = musicaService.findById(id);
        if (musicaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(musicaOpt.get());
    }

    @PostMapping
    public ResponseEntity<Object> registrarMusica(@RequestBody @Valid MusicaDTO musicaDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        Musica musica = new Musica();
        BeanUtils.copyProperties(musicaDTO, musica);

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

    @GetMapping("/stream/{artista}/{album}/{musica}")
    public ResponseEntity<Resource> streamMusica(@PathVariable String artista, @PathVariable String album,
            @PathVariable String musica) {
        try {
            Path arquivo = Paths.get(base, artista, album, musica);

            if (!Files.exists(arquivo)) {
                throw new RuntimeException("Arquivo nao encontrado: " + arquivo.toString());
            }

            Resource resource = new UrlResource(arquivo.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; arquivo=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Arquivo nao legivel: " + arquivo.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}