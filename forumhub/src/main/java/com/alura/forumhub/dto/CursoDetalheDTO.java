package com.alura.forumhub.dto;


import com.alura.forumhub.model.Curso;

public record CursoDetalheDTO(
        Long id,
        String nome,
        String categoria
) {
    public CursoDetalheDTO(Curso curso) {
        this(curso.getId(), curso.getNome(), curso.getCategoria());
    }
}
