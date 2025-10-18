package br.unibh.sdm.unimusic_playlist.entidades;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class MusicaPlaylist {
    private String id;
    private String titulo;
    private String artistaNome;

    public MusicaPlaylist() {}

    public MusicaPlaylist(String id, String titulo, String artistaNome) {
        this.id = id;
        this.titulo = titulo;
        this.artistaNome = artistaNome;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getArtistaNome() { return artistaNome; }
    public void setArtistaNome(String artistaNome) { this.artistaNome = artistaNome; }
}
