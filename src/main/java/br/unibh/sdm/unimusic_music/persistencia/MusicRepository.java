package br.unibh.sdm.unimusic_music.persistencia;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import br.unibh.sdm.unimusic_music.entidades.Music;

@EnableScan
public interface MusicRepository extends CrudRepository<Music, String>{

    List<Music> findByTitulo(String titulo);

}
