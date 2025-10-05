package com.project.unimusic.controllers;

import com.project.unimusic.entidades.Musica;
import com.project.unimusic.services.MusicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.UUID;

import java.util.List;
import java.util.Optional;
import com.project.unimusic.dto.ArtistaDTO;
import com.project.unimusic.dto.AlbumDTO;
import com.project.unimusic.dto.MusicaDTO;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    @Autowired
    private MusicaService musicaService;

    @Value("${base}")
    private String base;

    @GetMapping
    public List<MusicaDTO> getAllMusicas() {
        return musicaService.findAll().stream().map(musica -> {
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