package com.project.unimusic.controllers;

import com.project.unimusic.entidades.Artista;
import com.project.unimusic.controllers.ArtistaController;
import com.project.unimusic.services.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    @Autowired
    private ArtistaService artistaService;

    @PostMapping
    public Artista createArtists(@RequestBody Artista artistas) {
        return artistaService.save(artistas);
    }

    @GetMapping
    public List<Artista> getAllArtistas() {
        return artistaService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Artista> getArtistaById(@PathVariable UUID id) {
        return artistaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}