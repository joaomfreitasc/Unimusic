package br.unibh.sdm.unimusic_playlist.dto;

public class MusicaDetalheDTO {
    private String id;
    private String titulo;
    private String artistaNome;

    public String getId() {
        return id;
    }
    public void setId(String id) {
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