package com.alura.forumhub.infra.security;

import com.alura.forumhub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Marca a classe como um serviço Spring
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository; // Injeta o repositório de usuário

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Este método é chamado pelo Spring Security para carregar os detalhes do usuário
        // O 'username' aqui será o email do usuário
        UserDetails userDetails = repository.findByEmail(username); // Busca o usuário pelo email

        if (userDetails == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        // Linha de depuração para verificar as autoridades carregadas
        System.out.println("Autoridades carregadas para " + username + ": " + userDetails.getAuthorities());

        return userDetails;
    }
}
