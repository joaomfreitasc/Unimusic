package br.unibh.sdm.unimusic_playlist.dto;

import java.util.List;
public class PlaylistDto {
    private String id;
    private String nome;
    private String usuarioId;
    private List<String> musicasIds;

    public PlaylistDto() {}

    public PlaylistDto(String nome, String usuarioId, List<String> musicasIds) {
        this.nome = nome;
        this.usuarioId = usuarioId;
        this.musicasIds = musicasIds;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
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
    public List<String> getMusicasIds() {
        return musicasIds;
    }
    public void setMusicasIds(List<String> musicasIds) {
        this.musicasIds = musicasIds;
    }
}
