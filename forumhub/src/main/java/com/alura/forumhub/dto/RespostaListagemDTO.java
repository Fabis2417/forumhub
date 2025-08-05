package com.alura.forumhub.dto;

import com.alura.forumhub.model.Resposta;
import java.time.LocalDateTime;

public record RespostaListagemDTO(
        Long id,
        String mensagem,
        LocalDateTime dataCriacao,
        Boolean solucao,
        Long idTopico, // Retorna apenas o ID do tópico
        UsuarioDetalheDTO autor // Retorna o DTO do autor
) {
    public RespostaListagemDTO(Resposta resposta) {
        this(resposta.getId(), resposta.getMensagem(), resposta.getDataCriacao(),
                resposta.getSolucao(), resposta.getTopico().getId(), new UsuarioDetalheDTO(resposta.getAutor()));
    }
}
