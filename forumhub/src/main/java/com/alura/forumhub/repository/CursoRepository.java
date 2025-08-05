package com.alura.forumhub.repository;


import com.alura.forumhub.model.Curso; // Importa a entidade Curso
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface é um componente de repositório do Spring
public interface CursoRepository extends JpaRepository<Curso, Long> {
    // JpaRepository já fornece métodos CRUD básicos para a entidade Curso.
    // Você pode adicionar métodos de consulta personalizados aqui, se necessário.

    // Exemplo: encontrar um curso pelo nome
    Curso findByNome(String nome);
}