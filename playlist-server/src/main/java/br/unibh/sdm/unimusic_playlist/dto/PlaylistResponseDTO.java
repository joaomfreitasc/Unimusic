package br.unibh.sdm.unimusic_playlist.dto;

import java.util.List;

public class PlaylistResponseDTO {
    private String id_playlist;
    private String nome;
    private String id_user;
    private List<MusicaDTO> vetor_musica;
    
    public String getId_playlist() {
        return id_playlist;
    }
    public void setId_playlist(String id_playlist) {
        this.id_playlist = id_playlist;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getId_user() {
        return id_user;
    }
    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
    public List<MusicaDTO> getVetor_musica() {
        return vetor_musica;
    }
    public void setVetor_musica(List<MusicaDTO> vetor_musica) {
        this.vetor_musica = vetor_musica;
    }

}
