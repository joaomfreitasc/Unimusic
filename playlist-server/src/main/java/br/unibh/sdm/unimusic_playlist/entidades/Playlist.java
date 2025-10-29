package br.unibh.sdm.unimusic_playlist.entidades;

import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Playlist {
    private String id;
    private String nome;
    private String usuarioId;
    private List<MusicaPlaylist> musicas;

    public Playlist() {}

    public Playlist(String nome, String usuarioId, List<MusicaPlaylist> musicas) {
        this.nome = nome;
        this.usuarioId = usuarioId;
        this.musicas = musicas;
    }

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public List<MusicaPlaylist> getMusicas() { return musicas; }
    public void setMusicas(List<MusicaPlaylist> musicas) { this.musicas = musicas; }
}
