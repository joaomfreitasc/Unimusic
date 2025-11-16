package br.unibh.sdm.unimusic_playlist.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import br.unibh.sdm.unimusic_playlist.entidades.Playlist;
import br.unibh.sdm.unimusic_playlist.entidades.MusicaPlaylist;
import br.unibh.sdm.unimusic_playlist.negocio.PlaylistService;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistCriarDTO;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistDetalheDTO;
import br.unibh.sdm.unimusic_playlist.dto.PlaylistAtualizarDTO;

/**
 * Classe de testes para a entidade Playlist com DynamoDB.
 * <br>
 * IMPORTANTE: Este teste requer:
 * 1. Credenciais AWS validas configuradas
 * 2. Tabela 'playlists' criada no DynamoDB
 * 3. Permissoes IAM adequadas
 * <br>
 * @author Joao Marcos
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class PlaylistApplicationTests {

    private static Logger LOGGER = LoggerFactory.getLogger(PlaylistApplicationTests.class);

    @Autowired
    private PlaylistService playlistService;

    // Variaveis para armazenar IDs durante os testes
    private static String playlistIdTeste1;
    private static String playlistIdTeste2;
    private static String playlistIdTeste3;
    private static String musicaIdTeste;

    @Test
    @Order(1)
    public void teste1Criacao() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 1: CRIACAO DE PLAYLISTS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Criar Playlist 1 - Rock Nacional
        // ============================================
        LOGGER.info("PASSO 1: Criando playlist 'Rock Nacional - TESTE'");
        
        PlaylistCriarDTO dto1 = new PlaylistCriarDTO();
        dto1.setNome("Rock Nacional - TESTE");
        dto1.setUsuarioId("user-teste-123");
        
        PlaylistDetalheDTO playlist1 = playlistService.criar(dto1);
        
        assertNotNull(playlist1.getId(), "ID da playlist nao deve ser nulo");
        playlistIdTeste1 = playlist1.getId();
        
        LOGGER.info("Playlist criada com ID: {}", playlist1.getId());
        LOGGER.info("  - Nome: {}", playlist1.getNome());
        LOGGER.info("  - Usuario: {}", playlist1.getUsuarioId());
        LOGGER.info("  - Total de musicas: {}", playlist1.getMusicas().size());
        
        // Adicionar musicas na playlist
        musicaIdTeste = UUID.randomUUID().toString();
        playlistService.adicionarMusica(playlistIdTeste1, musicaIdTeste, "Eduardo e Monica", "Legiao Urbana");
        playlistService.adicionarMusica(playlistIdTeste1, UUID.randomUUID().toString(), "Tempo Perdido", "Legiao Urbana");
        
        PlaylistDetalheDTO playlistComMusicas = playlistService.obterPorId(playlistIdTeste1);
        LOGGER.info("Musicas adicionadas: {}", playlistComMusicas.getMusicas().size());
        for (var m : playlistComMusicas.getMusicas()) {
            LOGGER.info("    * {} - {}", m.getTitulo(), m.getArtistaNome());
        }

        // ============================================
        // PASSO 2: Criar Playlist 2 - MPB
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Criando playlist 'MPB Classica - TESTE'");
        LOGGER.info("========================================");
        
        PlaylistCriarDTO dto2 = new PlaylistCriarDTO();
        dto2.setNome("MPB Classica - TESTE");
        dto2.setUsuarioId("user-teste-123");
        
        PlaylistDetalheDTO playlist2 = playlistService.criar(dto2);
        
        assertNotNull(playlist2.getId(), "ID da playlist nao deve ser nulo");
        playlistIdTeste2 = playlist2.getId();
        
        LOGGER.info("Playlist criada com ID: {}", playlist2.getId());
        LOGGER.info("  - Nome: {}", playlist2.getNome());
        LOGGER.info("  - Total de musicas: {}", playlist2.getMusicas().size());
        
        // Adicionar uma musica
        playlistService.adicionarMusica(playlistIdTeste2, UUID.randomUUID().toString(), "Aguas de Marco", "Tom Jobim");

        // ============================================
        // PASSO 3: Criar Playlist 3 - Vazia
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 3: Criando playlist vazia 'Favoritas - TESTE'");
        LOGGER.info("========================================");
        
        PlaylistCriarDTO dto3 = new PlaylistCriarDTO();
        dto3.setNome("Favoritas - TESTE");
        dto3.setUsuarioId("user-teste-456");
        
        PlaylistDetalheDTO playlist3 = playlistService.criar(dto3);
        
        assertNotNull(playlist3.getId(), "ID da playlist nao deve ser nulo");
        playlistIdTeste3 = playlist3.getId();
        
        LOGGER.info("Playlist criada com ID: {}", playlist3.getId());
        LOGGER.info("  - Nome: {}", playlist3.getNome());
        LOGGER.info("  - Total de musicas: {}", playlist3.getMusicas().size());

        // ============================================
        // PASSO 4: Verificar OBTER TODAS
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 4: Testando obterTodasPlaylists()");
        LOGGER.info("========================================");
        
        List<Playlist> todasPlaylists = playlistService.obterTodasPlaylists();
        assertNotNull(todasPlaylists, "Lista de playlists nao deve ser nula");
        assertTrue(todasPlaylists.size() >= 3, "Deve ter pelo menos 3 playlists");
        
        LOGGER.info("Total de playlists no DynamoDB: {}", todasPlaylists.size());
        int contador = 1;
        for (Playlist p : todasPlaylists) {
            if (p.getNome() != null && p.getNome().contains("- TESTE")) {
                LOGGER.info("  {}. {} - {} musicas (Usuario: {})", 
                    contador++,
                    p.getNome(),
                    p.getMusicas() != null ? p.getMusicas().size() : 0,
                    p.getUsuarioId());
            }
        }

        LOGGER.info("========================================");
        LOGGER.info("TESTE 1: CRIACAO - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================\n");
    }

    @Test
    @Order(2)
    public void teste2Consulta() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 2: CONSULTA DE PLAYLISTS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Buscar por ID
        // ============================================
        LOGGER.info("PASSO 1: Buscando playlist por ID");
        
        // Buscar todas as playlists de teste
        List<Playlist> playlistsTeste = playlistService.obterTodasPlaylists();
        
        Playlist playlistParaBuscar = null;
        for (Playlist p : playlistsTeste) {
            if (p.getNome() != null && p.getNome().contains("- TESTE")) {
                playlistParaBuscar = p;
                break;
            }
        }
        
        assertNotNull(playlistParaBuscar, "Deve existir playlists de teste");
        
        String idBusca = playlistParaBuscar.getId();
        LOGGER.info("Buscando playlist com ID: {}", idBusca);
        
        PlaylistDetalheDTO playlistEncontrada = playlistService.obterPorId(idBusca);
        assertNotNull(playlistEncontrada, "Playlist deve ser encontrada");
        
        LOGGER.info("Playlist encontrada:");
        LOGGER.info("  - ID: {}", playlistEncontrada.getId());
        LOGGER.info("  - Nome: {}", playlistEncontrada.getNome());
        LOGGER.info("  - Usuario: {}", playlistEncontrada.getUsuarioId());
        LOGGER.info("  - Total de musicas: {}", playlistEncontrada.getMusicas().size());

        // ============================================
        // PASSO 2: Buscar por usuario
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Buscando playlists do usuario 'user-teste-123'");
        LOGGER.info("========================================");
        
        List<PlaylistDetalheDTO> playlistsUsuario = playlistService.obterParaUsuario("user-teste-123");
        LOGGER.info("Playlists encontradas: {}", playlistsUsuario.size());
        
        for (PlaylistDetalheDTO p : playlistsUsuario) {
            LOGGER.info("  - {} ({} musicas)", p.getNome(), p.getMusicas().size());
        }
        
        assertTrue(playlistsUsuario.size() >= 2, "Usuario deve ter pelo menos 2 playlists");

        // ============================================
        // PASSO 3: Buscar por nome (contains)
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 3: Buscando playlists que contem 'Rock'");
        LOGGER.info("========================================");
        
        List<Playlist> todasPlaylists = playlistService.obterTodasPlaylists();
        long countRock = todasPlaylists.stream()
                .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains("rock"))
                .count();
        
        LOGGER.info("Playlists com 'Rock' no nome: {}", countRock);
        todasPlaylists.stream()
                .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains("rock"))
                .forEach(p -> LOGGER.info("  - {}", p.getNome()));
        
        assertTrue(countRock >= 1, "Deve encontrar pelo menos 1 playlist de Rock");

        LOGGER.info("========================================");
        LOGGER.info("TESTE 2: CONSULTA - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================\n");
    }

    @Test
    @Order(3)
    public void teste3Atualizacao() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 3: ATUALIZACAO DE PLAYLISTS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Buscar playlist para atualizar
        // ============================================
        LOGGER.info("PASSO 1: Buscando playlist 'Rock Nacional - TESTE'");
        
        List<Playlist> playlists = playlistService.obterTodasPlaylists();
        Playlist playlistParaAtualizar = null;
        for (Playlist p : playlists) {
            if (p.getNome() != null && p.getNome().equals("Rock Nacional - TESTE")) {
                playlistParaAtualizar = p;
                break;
            }
        }
        
        assertNotNull(playlistParaAtualizar, "Playlist deve existir");
        
        LOGGER.info("Playlist encontrada:");
        LOGGER.info("  - ID: {}", playlistParaAtualizar.getId());
        LOGGER.info("  - Nome ANTES: {}", playlistParaAtualizar.getNome());
        LOGGER.info("  - Total de musicas ANTES: {}", playlistParaAtualizar.getMusicas().size());

        // ============================================
        // PASSO 2: Adicionar nova musica
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Adicionando musica 'Faroeste Caboclo' na playlist");
        LOGGER.info("========================================");
        
        int totalMusicasAntes = playlistParaAtualizar.getMusicas().size();
        
        playlistService.adicionarMusica(
            playlistParaAtualizar.getId(), 
            UUID.randomUUID().toString(), 
            "Faroeste Caboclo", 
            "Legiao Urbana"
        );
        
        PlaylistDetalheDTO playlistAtualizada = playlistService.obterPorId(playlistParaAtualizar.getId());
        
        LOGGER.info("Playlist atualizada:");
        LOGGER.info("  - Total de musicas ANTES: {}", totalMusicasAntes);
        LOGGER.info("  - Total de musicas DEPOIS: {}", playlistAtualizada.getMusicas().size());
        
        assertEquals(totalMusicasAntes + 1, playlistAtualizada.getMusicas().size(), 
            "Playlist deve ter mais 1 musica");

        // ============================================
        // PASSO 3: Atualizar nome da playlist
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 3: Atualizando nome da playlist");
        LOGGER.info("========================================");
        
        String nomeAntigo = playlistAtualizada.getNome();
        String nomeNovo = "Rock Nacional Atualizado - TESTE";
        
        PlaylistAtualizarDTO dtoAtualizar = new PlaylistAtualizarDTO();
        dtoAtualizar.setNome(nomeNovo);
        
        playlistService.atualizar(playlistParaAtualizar.getId(), dtoAtualizar);
        
        PlaylistDetalheDTO playlistComNomeNovo = playlistService.obterPorId(playlistParaAtualizar.getId());
        
        LOGGER.info("Nome atualizado:");
        LOGGER.info("  - ANTES: {}", nomeAntigo);
        LOGGER.info("  - DEPOIS: {}", playlistComNomeNovo.getNome());
        
        assertEquals(nomeNovo, playlistComNomeNovo.getNome(), "Nome deve ser atualizado");

        // ============================================
        // PASSO 4: Verificar persistencia
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 4: Verificando se atualizacao foi persistida");
        LOGGER.info("========================================");
        
        PlaylistDetalheDTO playlistVerificacao = playlistService.obterPorId(playlistParaAtualizar.getId());
        assertEquals(nomeNovo, playlistVerificacao.getNome(),
            "Nome deve estar atualizado no DynamoDB");
        assertEquals(3, playlistVerificacao.getMusicas().size(),
            "Playlist deve ter 3 musicas");
        
        LOGGER.info("Atualizacao persistida com sucesso no DynamoDB!");

        LOGGER.info("========================================");
        LOGGER.info("TESTE 3: ATUALIZACAO - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================\n");
    }

    @Test
    @Order(4)
    public void teste4ExclusaoMusica() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 4: EXCLUSAO DE MUSICA DA PLAYLIST");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Buscar playlist
        // ============================================
        LOGGER.info("PASSO 1: Buscando playlist para remover musica");
        
        List<Playlist> playlists = playlistService.obterTodasPlaylists();
        Playlist playlistParaRemover = null;
        for (Playlist p : playlists) {
            if (p.getNome() != null && p.getNome().contains("Rock Nacional") && 
                p.getMusicas() != null && p.getMusicas().size() > 0) {
                playlistParaRemover = p;
                break;
            }
        }
        
        assertNotNull(playlistParaRemover, "Playlist deve existir");
        int totalMusicasAntes = playlistParaRemover.getMusicas().size();
        
        LOGGER.info("Playlist encontrada: {}", playlistParaRemover.getNome());
        LOGGER.info("Total de musicas ANTES: {}", totalMusicasAntes);

        // ============================================
        // PASSO 2: Remover primeira musica
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Removendo primeira musica da playlist");
        LOGGER.info("========================================");
        
        MusicaPlaylist musicaRemovida = playlistParaRemover.getMusicas().get(0);
        LOGGER.info("Removendo musica: {} - {}", musicaRemovida.getTitulo(), 
            musicaRemovida.getArtistaNome());
        
        playlistService.removerMusica(playlistParaRemover.getId(), musicaRemovida.getId());
        
        PlaylistDetalheDTO playlistAtualizada = playlistService.obterPorId(playlistParaRemover.getId());
        
        LOGGER.info("Musica removida com sucesso!");
        LOGGER.info("Total de musicas DEPOIS: {}", playlistAtualizada.getMusicas().size());
        
        assertEquals(totalMusicasAntes - 1, playlistAtualizada.getMusicas().size(),
            "Playlist deve ter 1 musica a menos");

        LOGGER.info("========================================");
        LOGGER.info("TESTE 4: EXCLUSAO DE MUSICA - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================\n");
    }

    @Test
    @Order(5)
    public void teste5ExclusaoPlaylist() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 5: EXCLUSAO DE PLAYLISTS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Contar playlists de teste
        // ============================================
        LOGGER.info("PASSO 1: Contando playlists de teste antes da exclusao");
        
        List<Playlist> todasPlaylists = playlistService.obterTodasPlaylists();
        long totalAntes = todasPlaylists.stream()
                .filter(p -> p.getNome() != null && p.getNome().contains("- TESTE"))
                .count();
        
        LOGGER.info("Total de playlists de TESTE antes: {}", totalAntes);
        assertTrue(totalAntes > 0, "Deve ter playlists de teste para excluir");

        // ============================================
        // PASSO 2: Excluir playlists de teste
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Excluindo playlists de teste");
        LOGGER.info("========================================");
        
        int contadorExclusoes = 0;
        for (Playlist playlist : todasPlaylists) {
            if (playlist.getNome() != null && playlist.getNome().contains("- TESTE")) {
                LOGGER.info("Excluindo: {} (ID: {})", playlist.getNome(), playlist.getId());
                playlistService.deletar(playlist.getId());
                contadorExclusoes++;
            }
        }
        
        LOGGER.info("Total de playlists excluidas: {}", contadorExclusoes);

        // ============================================
        // PASSO 3: Verificar exclusao
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 3: Verificando exclusao");
        LOGGER.info("========================================");
        
        List<Playlist> todasPlaylistsDepois = playlistService.obterTodasPlaylists();
        long totalDepois = todasPlaylistsDepois.stream()
                .filter(p -> p.getNome() != null && p.getNome().contains("- TESTE"))
                .count();
        
        LOGGER.info("Total de playlists de TESTE depois: {}", totalDepois);
        
        assertEquals(0, totalDepois, "Todas as playlists de teste devem ter sido excluidas");

        LOGGER.info("========================================");
        LOGGER.info("TESTE 5: EXCLUSAO - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================");
        LOGGER.info("");
        LOGGER.info("==========================================");
        LOGGER.info("  TODOS OS TESTES FORAM EXECUTADOS!     ");
        LOGGER.info("  - Criacao                             ");
        LOGGER.info("  - Consulta                            ");
        LOGGER.info("  - Atualizacao                         ");
        LOGGER.info("  - Exclusao de Musica                  ");
        LOGGER.info("  - Exclusao de Playlist                ");
        LOGGER.info("==========================================");
    }
}