package com.project.unimusic.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public class UsuarioDTO {

    private java.util.UUID id;

    @NotNull
    private String nomeUsuario;

    @NotNull
    private String email;

    @NotNull
    private String senha;

    public UsuarioDTO() {}

    public UsuarioDTO(String nomeUsuario, String email, String senha) {
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
    }

    public UsuarioDTO(UUID id, String nomeUsuario, String email) {
        this.id = id;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
