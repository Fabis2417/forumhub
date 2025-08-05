package com.alura.forumhub.infra.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.alura.forumhub.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service // Marca a classe como um serviço Spring
public class TokenService {

    @Value("${api.security.token.secret}") // Injeta o valor da variável de ambiente/propriedade
    private String secret; // Chave secreta para assinar o token

    // Construtor para depuração (opcional, mas útil para ver o valor da secret)
    public TokenService(@Value("${api.security.token.secret}") String secret) {
        this.secret = secret;
        // Linha de depuração para verificar se a secret está a ser carregada corretamente
        System.out.println("JWT Secret carregado: " + secret);
    }

    // Método para gerar o token JWT
    public String gerarToken(Usuario usuario) {
        try {
            // Define o algoritmo de assinatura com a chave secreta
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            // Constrói o token JWT
            return JWT.create()
                    .withIssuer("API ForumHub") // Emissor do token
                    .withSubject(usuario.getEmail()) // Assunto do token (identificador do usuário)
                    .withExpiresAt(dataExpiracao()) // Define a data de expiração do token
                    .sign(algoritmo); // Assina o token com o algoritmo e a chave secreta
        } catch (JWTCreationException exception){
            // Lança uma exceção em caso de erro na criação do token
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    // Método para validar o token JWT e retornar o assunto (email do usuário)
    public String getSubject(String tokenJWT) {
        try {
            // Define o algoritmo de verificação com a chave secreta
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            // Constrói o verificador do token
            return JWT.require(algoritmo)
                    .withIssuer("API ForumHub") // Verifica o emissor
                    .build()
                    .verify(tokenJWT) // Verifica o token
                    .getSubject(); // Retorna o assunto (email do usuário)
        } catch (JWTVerificationException exception){
            // CORREÇÃO AQUI: Lança uma exceção com a mensagem detalhada da JWTVerificationException
            throw new RuntimeException("Token JWT inválido ou expirado! Detalhes: " + exception.getMessage());
        }
    }

    // Método auxiliar para definir a data de expiração (2 horas a partir de agora)
    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // Fuso horário de Brasília
    }
}
