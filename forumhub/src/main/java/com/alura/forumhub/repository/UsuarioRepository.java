package com.alura.forumhub.repository;

import com.alura.forumhub.model.Usuario; // Importa a entidade Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface é um componente de repositório do Spring
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // JpaRepository já fornece métodos CRUD básicos para a entidade Usuario.
    // Você pode adicionar métodos de consulta personalizados aqui, se necessário.

    // Exemplo: encontrar um usuário pelo email (útil para login)
    Usuario findByEmail(String email);
}