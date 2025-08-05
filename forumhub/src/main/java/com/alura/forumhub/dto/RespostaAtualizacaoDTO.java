package com.alura.forumhub.dto;

import jakarta.validation.constraints.NotBlank;

public record RespostaAtualizacaoDTO(
        @NotBlank(message = "A mensagem não pode estar em branco na atualização")
        String mensagem
) {
}
