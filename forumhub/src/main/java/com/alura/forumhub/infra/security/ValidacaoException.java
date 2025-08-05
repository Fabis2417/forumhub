package com.alura.forumhub.infra.security;

// Importa a classe HttpStatus para poder retornar códigos de status HTTP específicos
import org.springframework.http.HttpStatus;
// Importa a anotação ResponseStatus para associar um status HTTP à exceção
import org.springframework.web.bind.annotation.ResponseStatus;

// Define que esta é uma exceção de validação
// @ResponseStatus(HttpStatus.BAD_REQUEST) indica que, se esta exceção for lançada
// e não for tratada por um @ExceptionHandler, o Spring automaticamente retornará
// um status HTTP 400 Bad Request.
// Isso é uma boa prática para exceções de regras de negócio.
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidacaoException extends RuntimeException {

    // Construtor que recebe uma mensagem de erro
    public ValidacaoException(String mensagem) {
        super(mensagem); // Chama o construtor da classe pai (RuntimeException) com a mensagem
    }
}
