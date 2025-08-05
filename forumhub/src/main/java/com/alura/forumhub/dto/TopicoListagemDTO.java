package com.alura.forumhub.dto;

import com.alura.forumhub.model.StatusTopico;
import com.alura.forumhub.model.Topico;

import java.time.LocalDateTime;

public record TopicoListagemDTO(
        Long id,
        String titulo,
        String mensagem,
        LocalDateTime dataCriacao,
        StatusTopico status,
        UsuarioDetalheDTO autor, // Agora é um DTO de Usuario
        CursoDetalheDTO curso // Agora é um DTO de Curso
) {
    // Construtor que recebe uma entidade Topico e mapeia para o DTO
    public TopicoListagemDTO(Topico topico) {
        this(topico.getId(), topico.getTitulo(), topico.getMensagem(), topico.getDataCriacao(),
                topico.getStatus(),
                // Verifica se o autor não é nulo antes de criar o DTO de autor
                topico.getAutor() != null ? new UsuarioDetalheDTO(topico.getAutor()) : null,
                // Verifica se o curso não é nulo antes de criar o DTO de curso
                topico.getCurso() != null ? new CursoDetalheDTO(topico.getCurso()) : null);
    }
}