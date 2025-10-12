package com.project.unimusic.dto;

import java.util.UUID;

public class MusicaRegisterDTO {
    private UUID id;
    private String titulo;
    private Long duracao;
    private UUID artistaId;
    private UUID albumId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Long getDuracao() {
        return duracao;
    }

    public void setDuracao(Long duracao) {
        this.duracao = duracao;
    }

    public UUID getArtistaId() {
        return artistaId;
    }

    public void setArtistaId(UUID artistaId) {
        this.artistaId = artistaId;
    }

    public UUID getAlbumId() {
        return albumId;
    }

    public void setAlbumId(UUID albumId) {
        this.albumId = albumId;
    }
}
