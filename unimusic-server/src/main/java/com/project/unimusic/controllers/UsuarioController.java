package com.project.unimusic.controllers;

import com.project.unimusic.entidades.Usuario;
import com.project.unimusic.services.UsuarioService;
import com.project.unimusic.dto.UsuarioDTO;
import com.project.unimusic.dto.LoginDTO;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.findAll());

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUsuarioById(@PathVariable UUID id) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }
        Usuario usuario = usuarioOpt.get();
        UsuarioDTO usuarioRetorno = new UsuarioDTO(usuario.getId(), usuario.getNomeUsuario(), usuario.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRetorno);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrarUsuario(@RequestBody @Valid UsuarioDTO usuarioDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        if (usuarioService.findByNomeUsuario(usuarioDTO.getNomeUsuario()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome de usuario já está em uso");
        }

        if (usuarioService.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já está em uso");
        }

        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, usuario);

        Usuario usuarioSalvo = usuarioService.save(usuario);
        if (usuarioSalvo == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        UsuarioDTO usuarioRetorno = new UsuarioDTO(usuarioSalvo.getId(), usuarioSalvo.getNomeUsuario(),
                usuarioSalvo.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRetorno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUsuario(@PathVariable UUID id, @RequestBody @Valid UsuarioDTO usuarioDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        Usuario usuarioDetails = new Usuario();
        usuarioDetails.setNomeUsuario(usuarioDTO.getNomeUsuario());
        usuarioDetails.setEmail(usuarioDTO.getEmail());
        usuarioDetails.setSenha(usuarioDTO.getSenha());

        Optional<Usuario> updatedUsuarioOpt = usuarioService.update(id, usuarioDetails);

        if (updatedUsuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario updatedUsuario = updatedUsuarioOpt.get();
        UsuarioDTO usuarioRetorno = new UsuarioDTO(updatedUsuario.getId(), updatedUsuario.getNomeUsuario(),
                updatedUsuario.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRetorno);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUsuario(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        Usuario usuarioAutenticado = usuarioService.autenticar(loginDTO.getNomeUsuario(), loginDTO.getSenha());
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha ou nome de usuário inválidos");
        }

        UsuarioDTO usuarioRetorno = new UsuarioDTO(usuarioAutenticado.getId(), usuarioAutenticado.getNomeUsuario(),
                usuarioAutenticado.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRetorno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable UUID id) {
        boolean deleted = usuarioService.deleteById(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}