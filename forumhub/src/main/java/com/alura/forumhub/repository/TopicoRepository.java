package com.alura.forumhub.repository;

import com.alura.forumhub.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface é um componente de repositório do Spring
public interface TopicoRepository extends JpaRepository<Topico, Long> {
    // JpaRepository já fornece métodos CRUD básicos como save, findById, findAll, deleteById.
    // Você pode adicionar métodos personalizados aqui se precisar de consultas mais complexas.

    // Exemplo de método de consulta personalizado (opcional):
    boolean existsByTituloAndMensagem(String titulo, String mensagem);
}
