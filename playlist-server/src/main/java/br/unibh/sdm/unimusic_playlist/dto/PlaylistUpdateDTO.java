package br.unibh.sdm.unimusic_playlist.dto;

public class PlaylistUpdateDTO {
    private String nome;

    public PlaylistUpdateDTO() {}

    public PlaylistUpdateDTO(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
}