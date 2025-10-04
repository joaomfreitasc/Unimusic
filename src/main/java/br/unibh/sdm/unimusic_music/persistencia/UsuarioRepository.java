package br.unibh.sdm.unimusic_music.persistencia;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import br.unibh.sdm.unimusic_music.entidades.Usuario;
import br.unibh.sdm.unimusic_music.entidades.Usuario;

@EnableScan
public interface UsuarioRepository extends CrudRepository<Usuario, String> {

    List<Usuario> findByNome(String name);
    List<Usuario> findByEmail(String email);

}
