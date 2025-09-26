package br.unibh.sdm.unimusic_music.persistencia;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import br.unibh.sdm.unimusic_music.entidades.Musica;

@EnableScan
public interface MusicaRepository extends CrudRepository<Musica, String>{

    List<Musica> findByTitulo(String titulo);

}
