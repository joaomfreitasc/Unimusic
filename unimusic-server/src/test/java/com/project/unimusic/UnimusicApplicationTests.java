package com.project.unimusic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Optional;
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

import com.project.unimusic.entidades.Musica;
import com.project.unimusic.entidades.Artista;
import com.project.unimusic.entidades.Album;
import com.project.unimusic.repositories.MusicaRepository;
import com.project.unimusic.repositories.ArtistaRepository;
import com.project.unimusic.repositories.AlbumRepository;

/**
 * Classe de testes para a entidade Musica com PostgreSQL.
 * <br>
 * Para rodar, configure a variavel de ambiente: 
 * -Dspring.config.location=C:/Users/SEU_USUARIO/projeto_sdm/unimusic-server/
 * <br>
 * @author Joao Marcos
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UnimusicApplicationTests {

    private static Logger LOGGER = LoggerFactory.getLogger(UnimusicApplicationTests.class);

    @Autowired
    private MusicaRepository musicaRepository;

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private AlbumRepository albumRepository;

    // Variaveis para armazenar IDs durante os testes
    private static UUID artistaIdTeste;
    private static UUID albumIdTeste;
    private static UUID musicaIdTeste;

    @Test
    @Order(1)
    public void teste1Criacao() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 1: CRIACAO DE MUSICAS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Criar Artista
        // ============================================
        LOGGER.info("PASSO 1: Criando artista 'Legiao Urbana - TESTE'");
        Artista artista = new Artista();
        artista.setNome("Legiao Urbana - TESTE");
        artista = artistaRepository.save(artista);
        
        assertNotNull(artista.getId(), "ID do artista nao deve ser nulo");
        artistaIdTeste = artista.getId();
        LOGGER.info("Artista criado com ID: {}", artista.getId());

        // ============================================
        // PASSO 2: Criar Album
        // ============================================
        LOGGER.info("PASSO 2: Criando album 'Dois - TESTE'");
        Album album = new Album();
        album.setTitulo("Dois - TESTE");
        album.setArtista(artista);
        album = albumRepository.save(album);
        
        assertNotNull(album.getId(), "ID do album nao deve ser nulo");
        albumIdTeste = album.getId();
        LOGGER.info("Album criado com ID: {}", album.getId());

        // ============================================
        // PASSO 3: Criar Musica 1 - Eduardo e Monica
        // ============================================
        LOGGER.info("PASSO 3: Criando musica 'Eduardo e Monica - TESTE'");
        Musica musica1 = new Musica();
        musica1.setTitulo("Eduardo e Monica - TESTE");
        musica1.setDuracao(255); // 4min 15seg
        musica1.setArtista(artista);
        musica1.setAlbum(album);
        musica1 = musicaRepository.save(musica1);
        
        assertNotNull(musica1.getId(), "ID da musica nao deve ser nulo");
        musicaIdTeste = musica1.getId();
        LOGGER.info("Musica criada com ID: {}", musica1.getId());
        LOGGER.info("  - Titulo: {}", musica1.getTitulo());
        LOGGER.info("  - Duracao: {}s", musica1.getDuracao());
        LOGGER.info("  - Artista: {}", musica1.getArtista().getNome());

        // ============================================
        // PASSO 4: Criar Musica 2 - Tempo Perdido
        // ============================================
        LOGGER.info("PASSO 4: Criando musica 'Tempo Perdido - TESTE'");
        Musica musica2 = new Musica();
        musica2.setTitulo("Tempo Perdido - TESTE");
        musica2.setDuracao(298); // 4min 58seg
        musica2.setArtista(artista);
        musica2.setAlbum(album);
        musica2 = musicaRepository.save(musica2);
        
        assertNotNull(musica2.getId(), "ID da musica nao deve ser nulo");
        LOGGER.info("Musica criada com ID: {}", musica2.getId());
        LOGGER.info("  - Titulo: {}", musica2.getTitulo());
        LOGGER.info("  - Duracao: {}s", musica2.getDuracao());

        // ============================================
        // PASSO 5: Criar Musica 3 - Faroeste Caboclo
        // ============================================
        LOGGER.info("PASSO 5: Criando musica 'Faroeste Caboclo - TESTE'");
        Musica musica3 = new Musica();
        musica3.setTitulo("Faroeste Caboclo - TESTE");
        musica3.setDuracao(540); // 9min
        musica3.setArtista(artista);
        musica3.setAlbum(album);
        musica3 = musicaRepository.save(musica3);
        
        assertNotNull(musica3.getId(), "ID da musica nao deve ser nulo");
        LOGGER.info("Musica criada com ID: {}", musica3.getId());
        LOGGER.info("  - Titulo: {}", musica3.getTitulo());
        LOGGER.info("  - Duracao: {}s", musica3.getDuracao());

        // ============================================
        // PASSO 6: Verificar FINDALL
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 6: Testando FINDALL - Listar todas as musicas");
        LOGGER.info("========================================");
        List<Musica> todasMusicas = musicaRepository.findAll();
        assertNotNull(todasMusicas, "Lista de musicas nao deve ser nula");
        assertTrue(todasMusicas.size() >= 3, "Deve ter pelo menos 3 musicas");
        
        LOGGER.info("Total de musicas no banco: {}", todasMusicas.size());
        int contador = 1;
        for (Musica musica : todasMusicas) {
            if (musica.getTitulo().contains("- TESTE")) {
                LOGGER.info("  {}. {} - {} - {}s", 
                    contador++,
                    musica.getTitulo(), 
                    musica.getArtista().getNome(), 
                    musica.getDuracao());
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
        LOGGER.info("TESTE 2: CONSULTA DE MUSICAS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Buscar por ID
        // ============================================
        LOGGER.info("PASSO 1: Buscando musica por ID");
        
        // Primeiro, pegar uma musica de teste qualquer
        List<Musica> musicasTeste = musicaRepository.findByTituloContainingIgnoreCase("- TESTE");
        assertTrue(musicasTeste.size() > 0, "Deve existir musicas de teste");
        
        UUID idBusca = musicasTeste.get(0).getId();
        LOGGER.info("Buscando musica com ID: {}", idBusca);
        
        Optional<Musica> musicaEncontrada = musicaRepository.findById(idBusca);
        assertTrue(musicaEncontrada.isPresent(), "Musica deve ser encontrada");
        
        Musica musica = musicaEncontrada.get();
        LOGGER.info("Musica encontrada:");
        LOGGER.info("  - ID: {}", musica.getId());
        LOGGER.info("  - Titulo: {}", musica.getTitulo());
        LOGGER.info("  - Artista: {}", musica.getArtista().getNome());
        LOGGER.info("  - Duracao: {}s", musica.getDuracao());

        // ============================================
        // PASSO 2: Buscar por titulo (contains)
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Buscando musicas que contem 'Monica'");
        LOGGER.info("========================================");
        
        List<Musica> resultadoBusca = musicaRepository.findByTituloContainingIgnoreCase("Monica");
        LOGGER.info("Musicas encontradas: {}", resultadoBusca.size());
        
        for (Musica m : resultadoBusca) {
            LOGGER.info("  - {}", m.getTitulo());
        }
        
        assertTrue(resultadoBusca.size() >= 1, "Deve encontrar pelo menos 1 musica");
        boolean encontrouEduardo = resultadoBusca.stream()
            .anyMatch(m -> m.getTitulo().contains("Eduardo e Monica"));
        assertTrue(encontrouEduardo, "Deve encontrar 'Eduardo e Monica'");

        // ============================================
        // PASSO 3: Buscar musicas de teste
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 3: Buscando todas as musicas de TESTE");
        LOGGER.info("========================================");
        
        List<Musica> musicasTeste2 = musicaRepository.findByTituloContainingIgnoreCase("- TESTE");
        LOGGER.info("Total de musicas de teste: {}", musicasTeste2.size());
        assertTrue(musicasTeste2.size() >= 3, "Deve ter pelo menos 3 musicas de teste");

        LOGGER.info("========================================");
        LOGGER.info("TESTE 2: CONSULTA - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================\n");
    }

    @Test
    @Order(3)
    public void teste3Atualizacao() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 3: ATUALIZACAO DE MUSICAS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Buscar musica para atualizar
        // ============================================
        LOGGER.info("PASSO 1: Buscando musica 'Eduardo e Monica - TESTE'");
        
        List<Musica> musicas = musicaRepository.findByTituloContainingIgnoreCase("Eduardo e Monica - TESTE");
        assertTrue(musicas.size() > 0, "Musica deve existir");
        
        Musica musica = musicas.get(0);
        LOGGER.info("Musica encontrada:");
        LOGGER.info("  - ID: {}", musica.getId());
        LOGGER.info("  - Titulo ANTES: {}", musica.getTitulo());
        LOGGER.info("  - Duracao ANTES: {}s", musica.getDuracao());

        // ============================================
        // PASSO 2: Atualizar duracao
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Atualizando duracao da musica");
        LOGGER.info("========================================");
        
        long duracaoAntiga = musica.getDuracao();
        long duracaoNova = 260; // alterando de 255 para 260
        
        musica.setDuracao(duracaoNova);
        Musica musicaAtualizada = musicaRepository.save(musica);
        
        LOGGER.info("Musica atualizada:");
        LOGGER.info("  - Duracao ANTES: {}s", duracaoAntiga);
        LOGGER.info("  - Duracao DEPOIS: {}s", musicaAtualizada.getDuracao());
        
        assertEquals(duracaoNova, musicaAtualizada.getDuracao(), "Duracao deve ser atualizada");

        // ============================================
        // PASSO 3: Verificar persistencia
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 3: Verificando se atualizacao foi persistida");
        LOGGER.info("========================================");
        
        Optional<Musica> musicaVerificacao = musicaRepository.findById(musica.getId());
        assertTrue(musicaVerificacao.isPresent(), "Musica deve existir");
        assertEquals(duracaoNova, musicaVerificacao.get().getDuracao(),
            "Duracao deve estar atualizada no banco");
        
        LOGGER.info("Atualizacao persistida com sucesso no banco!");

        LOGGER.info("========================================");
        LOGGER.info("TESTE 3: ATUALIZACAO - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================\n");
    }

    @Test
    @Order(4)
    public void teste4Exclusao() {
        LOGGER.info("========================================");
        LOGGER.info("TESTE 4: EXCLUSAO DE MUSICAS");
        LOGGER.info("========================================");

        // ============================================
        // PASSO 1: Contar musicas de teste
        // ============================================
        LOGGER.info("PASSO 1: Contando musicas de teste antes da exclusao");
        
        List<Musica> musicasAntes = musicaRepository.findByTituloContainingIgnoreCase("- TESTE");
        int totalAntes = musicasAntes.size();
        LOGGER.info("Total de musicas de TESTE antes: {}", totalAntes);
        assertTrue(totalAntes > 0, "Deve ter musicas de teste para excluir");

        // ============================================
        // PASSO 2: Excluir musicas de teste
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 2: Excluindo musicas de teste");
        LOGGER.info("========================================");
        
        int contadorExclusoes = 0;
        for (Musica musica : musicasAntes) {
            LOGGER.info("Excluindo: {} (ID: {})", musica.getTitulo(), musica.getId());
            musicaRepository.delete(musica);
            contadorExclusoes++;
        }
        
        LOGGER.info("Total de musicas excluidas: {}", contadorExclusoes);

        // ============================================
        // PASSO 3: Verificar exclusao
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 3: Verificando exclusao");
        LOGGER.info("========================================");
        
        List<Musica> musicasDepois = musicaRepository.findByTituloContainingIgnoreCase("- TESTE");
        int totalDepois = musicasDepois.size();
        LOGGER.info("Total de musicas de TESTE depois: {}", totalDepois);
        
        assertEquals(0, totalDepois, "Todas as musicas de teste devem ter sido excluidas");

        // ============================================
        // PASSO 4: Limpar artistas e albuns de teste
        // ============================================
        LOGGER.info("========================================");
        LOGGER.info("PASSO 4: Limpando artistas e albuns de teste");
        LOGGER.info("========================================");
        
        // Excluir albuns de teste
        List<Album> albumsTeste = albumRepository.findAll();
        for (Album album : albumsTeste) {
            if (album.getTitulo() != null && album.getTitulo().contains("- TESTE")) {
                LOGGER.info("Excluindo album: {}", album.getTitulo());
                albumRepository.delete(album);
            }
        }
        
        // Excluir artistas de teste
        List<Artista> artistasTeste = artistaRepository.findAll();
        for (Artista artista : artistasTeste) {
            if (artista.getNome() != null && artista.getNome().contains("- TESTE")) {
                LOGGER.info("Excluindo artista: {}", artista.getNome());
                artistaRepository.delete(artista);
            }
        }

        LOGGER.info("========================================");
        LOGGER.info("TESTE 4: EXCLUSAO - CONCLUIDO COM SUCESSO!");
        LOGGER.info("========================================");
        LOGGER.info("");
        LOGGER.info("==========================================");
        LOGGER.info("  TODOS OS TESTES FORAM EXECUTADOS!     ");
        LOGGER.info("  - Criacao                             ");
        LOGGER.info("  - Consulta                            ");
        LOGGER.info("  - Atualizacao                         ");
        LOGGER.info("  - Exclusao                            ");
        LOGGER.info("==========================================");
    }
}