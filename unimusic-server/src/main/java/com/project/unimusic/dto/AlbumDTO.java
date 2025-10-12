package com.project.unimusic.dto;

import java.util.UUID;

public class AlbumDTO {
    private UUID id;
    private String titulo;
    private String dataDeLancamento;
    private UUID artistaId;

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

    public String getDataDeLancamento() {
        return dataDeLancamento;
    }

    public void setDataDeLancamento(String dataDeLancamento) {
        this.dataDeLancamento = dataDeLancamento;
    }

    public void setArtistaId(UUID artistaId) {
        this.artistaId = artistaId;
    }

    public UUID getArtistaId() {
        return artistaId;
    }
}
