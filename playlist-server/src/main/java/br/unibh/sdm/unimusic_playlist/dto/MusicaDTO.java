package br.unibh.sdm.unimusic_playlist.dto;

import java.util.UUID;

public class MusicaDTO {
    private UUID id;
    private String titulo;
    private String artistaNome;

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
    public String getArtistaNome() { 
        return artistaNome; 
    }
    public void setArtistaNome(String artistaNome) { 
        this.artistaNome = artistaNome; 
    }
}