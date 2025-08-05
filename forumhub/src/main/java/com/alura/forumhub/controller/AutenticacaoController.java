package com.alura.forumhub.controller;

import com.alura.forumhub.dto.DadosAutenticacaoDTO;
import com.alura.forumhub.infra.security.token.TokenService;
import com.alura.forumhub.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login") // Endpoint para autenticação
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager; // Injeta o AuthenticationManager do Spring Security

    @Autowired
    private TokenService tokenService; // Injeta o nosso TokenService

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacaoDTO dados) {
        // Cria um objeto de autenticação com as credenciais do usuário
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        // Autentica o usuário usando o AuthenticationManager
        var authentication = manager.authenticate(authenticationToken);

        // Se a autenticação for bem-sucedida, gera o token JWT
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        // Retorna o token JWT na resposta
        return ResponseEntity.ok(tokenJWT);
    }
}
