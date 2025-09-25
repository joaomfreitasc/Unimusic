package br.unibh.sdm.unimusic_music.negocio;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.unibh.sdm.unimusic_music.persistencia.UserRepository;
import br.unibh.sdm.unimusic_music.entidades.User;
import br.unibh.sdm.unimusic_music.exceptions.NotFoundException;

@Service
public class UserService {

    private static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
   
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        Iterable<User> users = userRepository.findAll();;
        return StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("O usuário com o id " + id + " não foi encontrado."));
    }

    public List<User> getUserByName(String name) {
        List<User> users = userRepository.findByName(name);
        if (!users.isEmpty()) {
            return users;
        }
        throw new NotFoundException("O usuário com o nome " + name + " não foi encontrado.");
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        if(!userRepository.existsById(id)) {
            throw new NotFoundException("O usuário com id " + id + " não foi encontrado.");
        }
        userRepository.deleteById(id);
    }
}
