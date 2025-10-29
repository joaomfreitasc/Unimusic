package br.unibh.sdm.unimusic_playlist.dto;

import java.util.List;

public class PlaylistCriarDTO {
    private String nome;
    private String usuarioId;
    private List<MusicaDetalheDTO> musicas;

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
    public List<MusicaDetalheDTO> getMusicas() {
        return musicas;
    }
    public void setMusicas(List<MusicaDetalheDTO> musicas) {
        this.musicas = musicas;
    }
}