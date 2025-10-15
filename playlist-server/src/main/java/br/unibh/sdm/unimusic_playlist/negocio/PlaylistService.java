package br.unibh.sdm.unimusic_playlist.negocio;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.unibh.sdm.unimusic_playlist.entidades.Playlist;
import br.unibh.sdm.unimusic_playlist.dto.MusicaDTO;
import br.unibh.sdm.unimusic_playlist.dto.MusicaWrapperDTO;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistCreateDTO;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistDto;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistResponseDTO;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistUpdateDTO;
import br.unibh.sdm.unimusic_playlist.persistencia.PlaylistRepository;
import br.unibh.sdm.unimusic_playlist.exceptions.NotFoundException;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private RestTemplate restTemplate;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public List<Playlist> getAllPlaylists() {
        return (List<Playlist>) playlistRepository.findAll();
    }
    public PlaylistDto getId(String id) {
        return playlistRepository.findById(id)
            .map(this::toDTO).orElseThrow(() -> new NotFoundException("Playlist não encontrada!"));
    }

    public Playlist getPlaylistId(String id) {
        return playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Playlist não encontrada!"));
    }

    public List<PlaylistDto> getforUser(String usuarioId) {
        List<Playlist> playlists = playlistRepository.findByUsuarioId(usuarioId);
        return playlists.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PlaylistDto create(PlaylistCreateDTO dto) {
        Playlist playlist = new Playlist();
        playlist.setNome(dto.getNome());
        playlist.setUsuarioId(dto.getUsuarioId());
        playlist.setMusicasIds(dto.getMusicasIds());

        Playlist saved = playlistRepository.save(playlist);
        return toDTO(saved);
    }

    public void addMusica(String playlistId, String musicaId) {
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new NotFoundException("Playlist não encontrada"));
        
        if (!playlist.getMusicasIds().contains(musicaId)) {
            playlist.getMusicasIds().add(musicaId);
            playlistRepository.save(playlist);
        }
    }

    public Playlist update(String id, PlaylistUpdateDTO dto) {
        Playlist playlist = playlistRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Playlist não encontrada!"));;;

        if(dto.getNome() != null) {
            playlist.setNome(dto.getNome());
        }
        Playlist saved = playlistRepository.save(playlist);
        return saved;
    }

    public void removeMusic(String playlistId, String musicaId) {
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new NotFoundException("Playlist não encontrada"));

        if (playlist.getMusicasIds().contains(musicaId)) {
            playlist.getMusicasIds().remove(musicaId);
            playlistRepository.save(playlist);
        }
    }

    public void delete(String id) {
        playlistRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Playlist não encontrada!"));
        playlistRepository.deleteById(id);
    }

    private PlaylistDto toDTO(Playlist playlist) {
        PlaylistDto dto = new PlaylistDto();
        dto.setId(playlist.getId());
        dto.setNome(playlist.getNome());
        dto.setUsuarioId(playlist.getUsuarioId());
        dto.setMusicasIds(playlist.getMusicasIds());
        return dto;
    }

    public MusicaDTO searchMusicForId(UUID musicaId) {
        try {
            String url = "http://localhost:8080/musicas/" + musicaId;
            MusicaWrapperDTO wrapper = restTemplate.getForObject(url, MusicaWrapperDTO.class);
            if (wrapper != null && wrapper.getMusicaDTO() != null) {
                MusicaWrapperDTO.MusicaInterna m = wrapper.getMusicaDTO();
                MusicaDTO dto = new MusicaDTO();
                dto.setId(m.getId());
                dto.setTitulo(m.getTitulo());
                dto.setArtistaNome(m.getArtista() != null ? m.getArtista().getNome() : null);
                return dto;
            }
            return null;
        } catch (Exception e) {
            System.out.println("Erro ao buscar música " + musicaId + ": " + e.getMessage());
            return null;
            }
    }

    public PlaylistResponseDTO return_music(Playlist playlist) {
        PlaylistResponseDTO response = new PlaylistResponseDTO();
        response.setId_playlist(playlist.getId());
        response.setId_user(playlist.getUsuarioId());
        response.setNome(playlist.getNome());

        List<MusicaDTO> musicas = playlist.getMusicasIds().stream()
                .map(UUID::fromString)
                .map(this::searchMusicForId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        response.setVetor_musica(musicas);
        return response;
    }

}