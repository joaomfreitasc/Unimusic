package com.project.unimusic.dto;

import jakarta.validation.constraints.NotNull;

public class LoginDTO {

    @NotNull
    private String nomeUsuario;

    @NotNull
    private String senha;

    public LoginDTO() {}

    public LoginDTO(String nomeUsuario, String senha) {
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
