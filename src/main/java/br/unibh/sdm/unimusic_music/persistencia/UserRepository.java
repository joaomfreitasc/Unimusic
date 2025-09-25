package br.unibh.sdm.unimusic_music.persistencia;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import br.unibh.sdm.unimusic_music.entidades.User;

@EnableScan
public interface UserRepository extends CrudRepository<User, String> {

    List<User> findByName(String name);
    List<User> findByEmail(String email);

}
