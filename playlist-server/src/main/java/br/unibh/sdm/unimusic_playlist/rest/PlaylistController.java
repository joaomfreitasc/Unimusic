package br.unibh.sdm.unimusic_playlist.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import br.unibh.sdm.unimusic_playlist.dto.*;
import br.unibh.sdm.unimusic_playlist.entidades.Playlist;
import br.unibh.sdm.unimusic_playlist.negocio.PlaylistService;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "playlist")
@CrossOrigin(origins = "http://localhost:8082")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public List<PlaylistDetalheDTO> obterTodasPlaylists() {
        return playlistService.obterTodasPlaylists().stream()
                .map(playlistService::paraDetalheDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PlaylistDetalheDTO obterPlaylist(@PathVariable String id) {
        return playlistService.obterPorId(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PlaylistDetalheDTO> obterPlaylistsPorUsuario(@PathVariable String usuarioId) {
        return playlistService.obterParaUsuario(usuarioId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public PlaylistDetalheDTO criar(@RequestBody @NotNull PlaylistCriarDTO dto) {
        return playlistService.criar(dto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{playlistId}/musica", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void adicionarMusica(@PathVariable String playlistId, @RequestBody @NotNull MusicaAdicionarDTO musicaDto) {
        playlistService.adicionarMusica(playlistId, musicaDto.getMusicaId(), musicaDto.getTitulo(),
                musicaDto.getArtistaNome());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public PlaylistDetalheDTO atualizarPlaylist(@PathVariable String id,
                                                 @RequestBody PlaylistAtualizarDTO dto) {
        Playlist playlistAtualizada = playlistService.atualizar(id, dto);
        return playlistService.paraDetalheDTO(playlistAtualizada);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{playlistId}")
    public void deletarPlaylist(@PathVariable String playlistId) {
        playlistService.deletar(playlistId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{playlistId}/musica/{musicaId}")
    public void deletarMusica(@PathVariable String playlistId, @PathVariable String musicaId) {
        playlistService.removerMusica(playlistId, musicaId);
    }
}
