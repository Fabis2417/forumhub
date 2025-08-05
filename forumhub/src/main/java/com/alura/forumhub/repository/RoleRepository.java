package com.alura.forumhub.repository;

import com.alura.forumhub.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface é um componente de repositório do Spring
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Método para encontrar uma Role pelo nome.
    // Este método é usado no UsuarioController para verificar se a role já existe
    // e para buscá-la ou criá-la.
    Role findByNome(String nome);
}
