package com.project.unimusic.dto;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public class ArtistaDTO {
    private UUID id;

    @NotNull
    private String nome;
    private List<AlbumDTO> albums;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<AlbumDTO> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumDTO> albums) {
        this.albums = albums;
    }
}
