package com.alura.forumhub.dto;

import jakarta.validation.constraints.NotBlank;

public record CursoCadastroDTO(
        @NotBlank(message = "O nome do curso não pode estar em branco")
        String nome,
        @NotBlank(message = "A categoria do curso não pode estar em branco")
        String categoria
) {
}
