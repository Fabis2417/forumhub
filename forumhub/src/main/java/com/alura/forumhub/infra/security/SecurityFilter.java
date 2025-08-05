package com.alura.forumhub.infra.security;

import com.alura.forumhub.infra.security.token.TokenService;
import com.alura.forumhub.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Importe a classe Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    // Instancie um Logger para esta classe
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);
        log.info("Requisição para: " + request.getRequestURI()); // Adiciona log da URI
        log.info("Token JWT recuperado no SecurityFilter: " + (tokenJWT != null ? "Sim" : "Não")); // Log de depuração

        if (tokenJWT != null) {
            try {
                var subject = tokenService.getSubject(tokenJWT);
                log.info("Subject (Email) extraído do token: " + subject); // Log de depuração

                if (subject != null) {
                    var usuario = usuarioRepository.findByEmail(subject);
                    if (usuario != null) {
                        var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Usuário autenticado com sucesso no SecurityContextHolder: " + subject); // Log de sucesso
                    } else {
                        log.warn("ATENÇÃO: Usuário não encontrado no banco de dados para o email: " + subject); // Log de aviso
                    }
                } else {
                    log.warn("AVISO: Subject do token é nulo, indica token inválido ou erro no TokenService.getSubject()."); // Log de aviso
                }
            } catch (Exception e) {
                // ESTE É O LOG MAIS IMPORTANTE! Ele pegará qualquer erro na validação do token.
                log.error("ERRO GRAVE na validação do token JWT no SecurityFilter: " + e.getMessage(), e);
            }
        } else {
            log.info("Nenhum token JWT encontrado no cabeçalho Authorization para esta requisição."); // Log para requisições sem token
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
