package com.alura.forumhub.dto;


import jakarta.validation.constraints.NotNull; // Adicionado para o ID do curso

public record TopicoAtualizacaoDTO(
        String titulo,
        String mensagem,
        Long idCurso // Agora espera o ID do Curso para atualização
) {
}
