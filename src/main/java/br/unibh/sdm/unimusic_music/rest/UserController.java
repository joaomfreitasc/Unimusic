package br.unibh.sdm.unimusic_music.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.unibh.sdm.unimusic_music.entidades.User;
import br.unibh.sdm.unimusic_music.negocio.UserService;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) String name) {
        if(name != null && !name.isEmpty()) {
            return userService.getUserByName(name);
        }
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="", consumes=MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody @NotNull User user) {
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return true;
    }
    
}
