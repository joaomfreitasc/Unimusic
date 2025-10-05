package com.project.unimusic.controllers;

import com.project.unimusic.entidades.Usuario;
import com.project.unimusic.services.UsuarioService;
import com.project.unimusic.dto.UsuarioDTO;
import com.project.unimusic.dto.LoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getUsuarios() {
        return usuarioService.findAll();
    }

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrarUsuario(@RequestBody @Valid UsuarioDTO usuarioDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos");
        }

        if (usuarioService.findByNomeUsuario(usuarioDTO.getNomeUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body("Nome de usuario já está em uso");
        }

        if (usuarioService.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já está em uso");
        }

        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, usuario);

        Usuario usuarioSalvo = usuarioService.save(usuario);
        if (usuarioSalvo == null) {
            return ResponseEntity.status(500).build();
        }

        UsuarioDTO usuarioRetorno = new UsuarioDTO(usuarioSalvo.getId(), usuarioSalvo.getNomeUsuario(),
                usuarioSalvo.getEmail());
        return ResponseEntity.ok(usuarioRetorno);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUsuario(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos");
        }

        Usuario usuarioAutenticado = usuarioService.autenticar(loginDTO.getNomeUsuario(), loginDTO.getSenha());
        if (usuarioAutenticado == null) {
            return ResponseEntity.badRequest().body("Senha ou nome de usuário inválidos");
        }

        UsuarioDTO usuarioRetorno = new UsuarioDTO(usuarioAutenticado.getId(), usuarioAutenticado.getNomeUsuario(),
                usuarioAutenticado.getEmail());
        return ResponseEntity.ok(usuarioRetorno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable UUID id) {
        boolean deleted = usuarioService.deleteById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
