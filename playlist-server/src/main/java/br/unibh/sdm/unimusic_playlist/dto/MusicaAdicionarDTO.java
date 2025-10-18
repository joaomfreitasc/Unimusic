package br.unibh.sdm.unimusic_playlist.dto;

public class MusicaAdicionarDTO {
    private String musicaId;
    private String titulo;
    private String artistaNome;

    public String getMusicaId() {
        return musicaId;
    }
    public void setMusicaId(String musicaId) {
        this.musicaId = musicaId;
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