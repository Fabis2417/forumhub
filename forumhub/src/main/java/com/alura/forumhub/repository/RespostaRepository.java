package com.alura.forumhub.repository;

import com.alura.forumhub.model.Resposta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface é um componente de repositório do Spring
public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    // Método para verificar se já existe uma resposta para um tópico específico
    // com a mesma mensagem (para evitar duplicatas, se desejar)
    boolean existsByTopicoIdAndMensagem(Long topicoId, String mensagem);

    // Método para buscar respostas por tópico, com paginação
    Page<Resposta> findByTopicoId(Long topicoId, Pageable paginacao);

    // Método para verificar se um tópico já tem uma resposta marcada como solução
    boolean existsByTopicoIdAndSolucaoTrue(Long topicoId);
}