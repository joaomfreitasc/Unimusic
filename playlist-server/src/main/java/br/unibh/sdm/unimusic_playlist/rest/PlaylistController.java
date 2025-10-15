package br.unibh.sdm.unimusic_playlist.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.unibh.sdm.unimusic_playlist.dto.PlaylistCreateDTO;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistDto;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistResponseDTO;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistUpdateDTO;
import br.unibh.sdm.unimusic_playlist.entidades.Playlist;
import br.unibh.sdm.unimusic_playlist.exceptions.NotFoundException;
import br.unibh.sdm.unimusic_playlist.negocio.PlaylistService;
import br.unibh.sdm.unimusic_playlist.persistencia.PlaylistRepository;
import software.amazon.awssdk.annotations.NotNull;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "playlist")
public class PlaylistController {

    @Autowired
    private PlaylistRepository playlistRepository;

    private final PlaylistService playlistService;
    
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping()
    public List<PlaylistResponseDTO> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return playlists.stream()
                .map(playlistService::return_music)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PlaylistResponseDTO getPlaylist(@PathVariable String id) {
        Playlist playlist = playlistService.getPlaylistId(id);
        return playlistService.return_music(playlist);
    }

    @GetMapping("/usuario/{usuarioId}")
    public PlaylistResponseDTO getUsuarioPlaylist(@PathVariable String usuarioId) {
        List<Playlist> playlist = playlistRepository.findByUsuarioId(usuarioId);
        if (playlist.isEmpty()) {
            throw new NotFoundException("Nenhuma playlist encontrada!");
        }
        PlaylistResponseDTO dto = playlistService.return_music(playlist.get(0));
        return dto;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="", consumes=MediaType.APPLICATION_JSON_VALUE)
    public PlaylistDto create(@RequestBody @NotNull PlaylistCreateDTO dto) {
        return playlistService.create(dto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{playlistId}/music/{musicaId}")
    public void addMusic(@PathVariable String playlistId, @PathVariable String musicaId) {
        playlistService.addMusica(playlistId, musicaId);

    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public PlaylistResponseDTO updatePlaylist(@PathVariable String id, @RequestBody PlaylistUpdateDTO update) {
        Playlist updated = playlistService.update(id, update);
        return playlistService.return_music(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{playlistId}")
    public boolean deletePlaylist(@PathVariable String playlistId) {
        playlistService.delete(playlistId);
        return true;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{playlistId}/music/{musicId}")
    public boolean deleteMusic(@PathVariable String playlistId, @PathVariable String musicId) {
        playlistService.removeMusic(playlistId, musicId);
        return true;
    }

}