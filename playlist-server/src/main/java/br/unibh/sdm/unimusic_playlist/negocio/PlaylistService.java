package br.unibh.sdm.unimusic_playlist.negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.unibh.sdm.unimusic_playlist.entidades.MusicaPlaylist;
import br.unibh.sdm.unimusic_playlist.entidades.Playlist;
import br.unibh.sdm.unimusic_playlist.dto.*;
import br.unibh.sdm.unimusic_playlist.exceptions.NotFoundException;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Service
public class PlaylistService {

    private final DynamoDbTable<Playlist> playlistTable;

    public PlaylistService(DynamoDbEnhancedClient enhancedClient) {
        this.playlistTable = enhancedClient.table("playlists", TableSchema.fromBean(Playlist.class));
    }

    public List<Playlist> obterTodasPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        playlistTable.scan().items().forEach(p -> {
            if (p.getMusicas() == null) p.setMusicas(new ArrayList<>());
            playlists.add(p);
        });
        return playlists;
    }

    public PlaylistDetalheDTO obterPorId(String id) {
        Playlist playlist = obterPlaylistPorId(id);
        return paraDetalheDTO(playlist);
    }

    public Playlist obterPlaylistPorId(String id) {
        Playlist playlist = playlistTable.getItem(r -> r.key(k -> k.partitionValue(id)));
        if (playlist == null) throw new NotFoundException("Playlist não encontrada!");
        if (playlist.getMusicas() == null) playlist.setMusicas(new ArrayList<>());
        return playlist;
    }

    public List<PlaylistDetalheDTO> obterParaUsuario(String usuarioId) {
        List<PlaylistDetalheDTO> resultado = new ArrayList<>();
        playlistTable.scan().items()
                .forEach(p -> {
                    if (usuarioId.equals(p.getUsuarioId())) {
                        if (p.getMusicas() == null) p.setMusicas(new ArrayList<>());
                        resultado.add(paraDetalheDTO(p));
                    }
                });
        return resultado;
    }

    public PlaylistDetalheDTO criar(PlaylistCriarDTO dto) {
        Playlist playlist = new Playlist();
        playlist.setId(UUID.randomUUID().toString());
        playlist.setNome(dto.getNome());
        playlist.setUsuarioId(dto.getUsuarioId());
        if (dto.getMusicas() != null) {
            playlist.setMusicas(dto.getMusicas().stream()
                    .map(this::paraMusicaPlaylist)
                    .collect(Collectors.toList()));
        } else {
            playlist.setMusicas(new ArrayList<>());
        }

        playlistTable.putItem(playlist);
        return paraDetalheDTO(playlist);
    }

    public void adicionarMusica(String playlistId, String musicaId, String titulo, String artistaNome) {
        Playlist playlist = obterPlaylistPorId(playlistId);

        if (playlist.getMusicas() == null) playlist.setMusicas(new ArrayList<>());

        boolean existe = playlist.getMusicas().stream().anyMatch(m -> m.getId().equals(musicaId));
        if (!existe) {
            playlist.getMusicas().add(new MusicaPlaylist(musicaId, titulo, artistaNome));
            playlistTable.putItem(playlist);
        }
    }

    public Playlist atualizar(String id, PlaylistAtualizarDTO dto) {
        Playlist playlist = obterPlaylistPorId(id);
        if (dto.getNome() != null) playlist.setNome(dto.getNome());
        playlistTable.putItem(playlist);
        return playlist;
    }

    public void removerMusica(String playlistId, String musicaId) {
        Playlist playlist = obterPlaylistPorId(playlistId);
        if (playlist.getMusicas() != null) {
            boolean removida = playlist.getMusicas().removeIf(m -> m.getId().equals(musicaId));
            if (removida) playlistTable.putItem(playlist);
        }
    }

    public void deletar(String id) {
        obterPlaylistPorId(id);
        playlistTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public PlaylistDetalheDTO paraDetalheDTO(Playlist playlist) {
        PlaylistDetalheDTO dto = new PlaylistDetalheDTO();
        dto.setId(playlist.getId());
        dto.setNome(playlist.getNome());
        dto.setUsuarioId(playlist.getUsuarioId());
        if (playlist.getMusicas() != null) {
            dto.setMusicas(playlist.getMusicas().stream()
                    .map(this::paraMusicaDetalheDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setMusicas(new ArrayList<>());
        }
        return dto;
    }

    public PlaylistRespostaDTO paraRespostaDTO(Playlist playlist) {
        PlaylistRespostaDTO dto = new PlaylistRespostaDTO();
        dto.setIdPlaylist(playlist.getId());
        dto.setIdUsuario(playlist.getUsuarioId());
        dto.setNome(playlist.getNome());
        if (playlist.getMusicas() != null) {
            dto.setMusicas(playlist.getMusicas().stream()
                    .map(this::paraMusicaDetalheDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setMusicas(new ArrayList<>());
        }
        return dto;
    }

    public MusicaDetalheDTO paraMusicaDetalheDTO(MusicaPlaylist musica) {
        MusicaDetalheDTO dto = new MusicaDetalheDTO();
        dto.setId(musica.getId());
        dto.setTitulo(musica.getTitulo());
        dto.setArtistaNome(musica.getArtistaNome());
        return dto;
    }

    public MusicaPlaylist paraMusicaPlaylist(MusicaDetalheDTO dto) {
        MusicaPlaylist musica = new MusicaPlaylist();
        musica.setId(dto.getId());
        musica.setTitulo(dto.getTitulo());
        musica.setArtistaNome(dto.getArtistaNome());
        return musica;
    }
}
