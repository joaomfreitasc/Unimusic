package com.project.unimusic.controllers;

import com.project.unimusic.dto.AlbumDTO;
import com.project.unimusic.entidades.Album;
import com.project.unimusic.services.AlbumService;
import com.project.unimusic.entidades.Artista;
import com.project.unimusic.services.ArtistaService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ArtistaService artistaService;

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        return ResponseEntity.status(HttpStatus.OK).body(albumService.findAll());

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAlbumById(@PathVariable UUID id) {
        Optional<Album> albumOpt = albumService.findById(id);
        if (albumOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(albumOpt.get());

    }

    @PostMapping
    public ResponseEntity<Object> registrarAlbum(@RequestBody @Valid AlbumDTO albumDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        Optional<Artista> artistaOpt = artistaService.findById(albumDTO.getArtistaId());

        if (artistaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Artista não encontrado com o ID fornecido.");
        }

        Album album = new Album();
        BeanUtils.copyProperties(albumDTO, album);

        album.setArtista(artistaOpt.get());

        Album albumSalvo = albumService.save(album);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAlbum(@PathVariable UUID id, @RequestBody @Valid AlbumDTO albumDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }
        Optional<Album> albumOpt = albumService.findById(id);
        if (albumOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album não encontrado.");
        }
        Album albumDetails = albumOpt.get();
        albumDetails.setTitulo(albumDTO.getTitulo());
        albumDetails.setDataDeLancamento(albumDTO.getDataDeLancamento());

        Optional<Album> updatedAlbumOpt = albumService.update(id, albumDetails);
        if (updatedAlbumOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album não encontrado.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(updatedAlbumOpt.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAlbumn(@PathVariable UUID id) {
        boolean deleted = albumService.deleteById(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
