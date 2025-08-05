package com.alura.forumhub.dto; // PACOTE CORRIGIDO AQUI

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopicoCadastroDTO(
        @NotBlank(message = "O título não pode estar em branco")
        String titulo,
        @NotBlank(message = "A mensagem não pode estar em branco")
        String mensagem,
        @NotNull(message = "O ID do autor não pode ser nulo") // Agora espera o ID do Usuario
        Long idAutor,
        @NotNull(message = "O ID do curso não pode ser nulo") // Agora espera o ID do Curso
        Long idCurso
) {
}
