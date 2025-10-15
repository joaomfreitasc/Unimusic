package br.unibh.sdm.unimusic_playlist.persistencia;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import br.unibh.sdm.unimusic_playlist.entidades.Playlist;

@EnableScan
public interface PlaylistRepository extends CrudRepository<Playlist, String> {
    List<Playlist> findByUsuarioId(String usuarioId);
}

