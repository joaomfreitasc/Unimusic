package br.unibh.sdm.unimusic_music.negocio;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.unibh.sdm.unimusic_music.persistencia.UsuarioRepository;
import br.unibh.sdm.unimusic_music.entidades.Usuario;
import br.unibh.sdm.unimusic_music.exceptions.NotFoundException;

@Service
public class UsuarioService {

    private static Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);
   
    private final UsuarioRepository userRepository;

    public UsuarioService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Usuario> getAllUsers() {
        Iterable<Usuario> users = userRepository.findAll();;
        return StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
    }

    public Usuario getUserById(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("O usuário com o id " + id + " não foi encontrado."));
    }

    public List<Usuario> getUserByName(String name) {
        List<Usuario> users = userRepository.findByNome(name);
        if (!users.isEmpty()) {
            return users;
        }
        throw new NotFoundException("O usuário com o nome " + name + " não foi encontrado.");
    }

    public Usuario createUser(Usuario user) {
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        if(!userRepository.existsById(id)) {
            throw new NotFoundException("O usuário com id " + id + " não foi encontrado.");
        }
        userRepository.deleteById(id);
    }
}
