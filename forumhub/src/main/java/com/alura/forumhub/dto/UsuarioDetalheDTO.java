package com.alura.forumhub.dto;


import com.alura.forumhub.model.Usuario;

public record UsuarioDetalheDTO(
        Long id,
        String nome,
        String email
) {
    public UsuarioDetalheDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
