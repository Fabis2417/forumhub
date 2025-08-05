package com.alura.forumhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RespostaCadastroDTO(
        @NotBlank(message = "A mensagem da resposta não pode estar em branco")
        String mensagem,
        @NotNull(message = "O ID do tópico não pode ser nulo")
        Long idTopico,
        @NotNull(message = "O ID do autor da resposta não pode ser nulo")
        Long idAutor
) {
}
