package com.project.unimusic.entidades;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "musicas")
public class Musica {
    // ...existing code...
    // Add this if you have a Set<Playlist> playlists in Musica
    // @ManyToMany(mappedBy = "musicas")
    // @com.fasterxml.jackson.annotation.JsonBackReference
    // private Set<Playlist> playlists;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String titulo;

    private long duracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Artista artista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Album album;

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

    public long getDuracao() {
        return duracao;
    }

    public void setDuracao(long duracao) {
        this.duracao = duracao;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

}
