package com.alura.forumhub.dto;

import com.alura.forumhub.model.Curso;

public record CursoListagemDTO(
        Long id,
        String nome,
        String categoria
) {
    public CursoListagemDTO(Curso curso) {
        this(curso.getId(), curso.getNome(), curso.getCategoria());
    }
}
