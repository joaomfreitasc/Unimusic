package com.project.unimusic.dto;

import java.util.UUID;

public class MusicaDTO {
    private UUID id;
    private String titulo;
    private Long duracao;
    private ArtistaDTO artista;
    private AlbumDTO album;

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

    public ArtistaDTO getArtista() {
        return artista;
    }

    public void setArtista(ArtistaDTO artista) {
        this.artista = artista;
    }

    public AlbumDTO getAlbum() {
        return album;
    }

    public void setAlbum(AlbumDTO album) {
        this.album = album;
    }

}
