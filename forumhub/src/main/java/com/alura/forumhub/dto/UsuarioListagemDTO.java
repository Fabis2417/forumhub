package com.alura.forumhub.dto;


import com.alura.forumhub.model.Usuario;

public record UsuarioListagemDTO(
        Long id,
        String nome,
        String email
) {
    public UsuarioListagemDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
