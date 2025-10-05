package com.project.unimusic.services;

import com.project.unimusic.entidades.Usuario;
import com.project.unimusic.repositories.UsuarioRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario save(Usuario usuario) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario autenticar(String nomeUsuario, String senha) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeUsuario(nomeUsuario);
        if (!usuarioOpt.isPresent() || !passwordEncoder.matches(senha, usuarioOpt.get().getSenha())) {
            return null;
        }
        return usuarioOpt.get();
    }

    public Optional<Usuario> findByNomeUsuario(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuario(nomeUsuario);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean deleteById(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }

}
