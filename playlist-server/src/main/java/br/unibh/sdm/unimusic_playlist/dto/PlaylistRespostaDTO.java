package br.unibh.sdm.unimusic_playlist.dto;

import java.util.List;

public class PlaylistRespostaDTO {
    private String idPlaylist;
    private String nome;
    private String idUsuario;
    private List<MusicaDetalheDTO> musicas;

    public String getIdPlaylist() {
        return idPlaylist;
    }
    public void setIdPlaylist(String idPlaylist) {
        this.idPlaylist = idPlaylist;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
    public List<MusicaDetalheDTO> getMusicas() {
        return musicas;
    }
    public void setMusicas(List<MusicaDetalheDTO> musicas) {
        this.musicas = musicas;
    }
}