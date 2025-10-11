package com.project.unimusic.controllers;

import com.project.unimusic.dto.ArtistaDTO;
import com.project.unimusic.entidades.Artista;
import com.project.unimusic.services.ArtistaService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    @Autowired
    private ArtistaService artistaService;

    @GetMapping
    public ResponseEntity<List<Artista>> getAllArtistas() {
        return ResponseEntity.status(HttpStatus.OK).body(artistaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getArtistaById(@PathVariable UUID id) {
        Optional<Artista> artistaOpt = artistaService.findById(id);
        if (artistaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(artistaOpt.get());
    }

    @PostMapping()
    public ResponseEntity<Object> registrarArtista(@RequestBody @Valid ArtistaDTO artistaDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        Artista artista = new Artista();
        BeanUtils.copyProperties(artistaDTO, artista);

        Artista artistaSalvo = artistaService.save(artista);
        return ResponseEntity.status(HttpStatus.CREATED).body(artistaSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateArtista(@PathVariable UUID id, @RequestBody @Valid ArtistaDTO artistaDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }
        Optional<Artista> artistaOpt = artistaService.findById(id);
        if (artistaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista não encontrado.");
        }
        Artista artistaDetails = artistaOpt.get();
        artistaDetails.setNome(artistaDTO.getNome());

        Optional<Artista> updatedArtistaOpt = artistaService.update(id, artistaDetails);
        if (updatedArtistaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista não encontrado.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(updatedArtistaOpt.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteArtista(@PathVariable UUID id) {
        boolean deleted = artistaService.deleteById(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}