package br.unibh.sdm.unimusic_playlist.rest;

import br.unibh.sdm.unimusic_playlist.dto.*;
import br.unibh.sdm.unimusic_playlist.entidades.Playlist;
import br.unibh.sdm.unimusic_playlist.exceptions.NotFoundException;
import br.unibh.sdm.unimusic_playlist.negocio.PlaylistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PlaylistController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaylistService playlistService;

    @Test
    public void testObterTodasPlaylists() throws Exception {
        Playlist playlist = new Playlist();
        playlist.setId("p1");
        playlist.setNome("My Playlist");

        PlaylistDetalheDTO dto = new PlaylistDetalheDTO();
        dto.setId("p1");
        dto.setNome("My Playlist");

        when(playlistService.obterTodasPlaylists()).thenReturn(List.of(playlist));
        when(playlistService.paraDetalheDTO(playlist)).thenReturn(dto);

        mockMvc.perform(get("/playlist")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("p1")))
                .andExpect(jsonPath("$[0].nome", is("My Playlist")));

        verify(playlistService, times(1)).obterTodasPlaylists();
        verify(playlistService, times(1)).paraDetalheDTO(any(Playlist.class));
    }

    @Test
    public void testObterPlaylist() throws Exception {
        PlaylistDetalheDTO dto = new PlaylistDetalheDTO();
        dto.setId("p1");
        dto.setNome("My Playlist");

        when(playlistService.obterPorId("p1")).thenReturn(dto);

        mockMvc.perform(get("/playlist/p1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("p1")))
                .andExpect(jsonPath("$.nome", is("My Playlist")));

        verify(playlistService, times(1)).obterPorId("p1");
    }

    @Test
    public void testObterPlaylistNaoEncontrada() throws Exception {
        when(playlistService.obterPorId("404")).thenThrow(new NotFoundException("Playlist não encontrada!"));

        mockMvc.perform(get("/playlist/404")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(playlistService, times(1)).obterPorId("404");
    }

    @Test
    public void testObterPlaylistsPorUsuario() throws Exception {
        PlaylistDetalheDTO dto = new PlaylistDetalheDTO();
        dto.setId("p1");
        dto.setUsuarioId("user1");

        when(playlistService.obterParaUsuario("user1")).thenReturn(List.of(dto));

        mockMvc.perform(get("/playlist/usuario/user1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("p1")))
                .andExpect(jsonPath("$[0].usuarioId", is("user1")));

        verify(playlistService, times(1)).obterParaUsuario("user1");
    }

    @Test
    public void testCriar() throws Exception {
        PlaylistCriarDTO criarDto = new PlaylistCriarDTO();
        criarDto.setNome("New Playlist");
        criarDto.setUsuarioId("user1");
        criarDto.setMusicas(new ArrayList<>());

        PlaylistDetalheDTO returnDto = new PlaylistDetalheDTO();
        returnDto.setId("new-uuid");
        returnDto.setNome("New Playlist");
        returnDto.setUsuarioId("user1");

        when(playlistService.criar(any(PlaylistCriarDTO.class))).thenReturn(returnDto);

        mockMvc.perform(post("/playlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criarDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("new-uuid")))
                .andExpect(jsonPath("$.nome", is("New Playlist")));

        verify(playlistService, times(1)).criar(any(PlaylistCriarDTO.class));
    }

    @Test
    public void testAdicionarMusica() throws Exception {
        MusicaAdicionarDTO musicaDto = new MusicaAdicionarDTO();
        musicaDto.setMusicaId("m1");
        musicaDto.setTitulo("Test Song");
        musicaDto.setArtistaNome("Test Artist");

        doNothing().when(playlistService).adicionarMusica("p1", "m1", "Test Song", "Test Artist");

        mockMvc.perform(post("/playlist/p1/musica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(musicaDto)))
                .andExpect(status().isCreated());

        verify(playlistService, times(1)).adicionarMusica("p1", "m1", "Test Song", "Test Artist");
    }

    @Test
    public void testAtualizarPlaylist() throws Exception {
        PlaylistAtualizarDTO atualizarDto = new PlaylistAtualizarDTO();
        atualizarDto.setNome("Updated Name");

        Playlist playlistAtualizada = new Playlist();
        playlistAtualizada.setId("p1");
        playlistAtualizada.setNome("Updated Name");

        PlaylistDetalheDTO dtoRetorno = new PlaylistDetalheDTO();
        dtoRetorno.setId("p1");
        dtoRetorno.setNome("Updated Name");

        when(playlistService.atualizar(eq("p1"), any(PlaylistAtualizarDTO.class))).thenReturn(playlistAtualizada);
        when(playlistService.paraDetalheDTO(playlistAtualizada)).thenReturn(dtoRetorno);

        mockMvc.perform(put("/playlist/p1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("p1")))
                .andExpect(jsonPath("$.nome", is("Updated Name")));

        verify(playlistService, times(1)).atualizar(eq("p1"), any(PlaylistAtualizarDTO.class));
        verify(playlistService, times(1)).paraDetalheDTO(playlistAtualizada);
    }

    @Test
    public void testDeletarPlaylist() throws Exception {
        doNothing().when(playlistService).deletar("p1");

        mockMvc.perform(delete("/playlist/p1"))
                .andExpect(status().isNoContent());

        verify(playlistService, times(1)).deletar("p1");
    }

    @Test
    public void testDeletarMusica() throws Exception {
        doNothing().when(playlistService).removerMusica("p1", "m1");

        mockMvc.perform(delete("/playlist/p1/musica/m1"))
                .andExpect(status().isNoContent());

        verify(playlistService, times(1)).removerMusica("p1", "m1");
    }
}