package com.alura.forumhub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "respostas") // Define o nome da tabela no banco de dados
@Entity(name = "Resposta") // Define o nome da entidade JPA
@Getter // Gera getters para todos os campos (Lombok)
@NoArgsConstructor // Gera um construtor sem argumentos (Lombok)
@AllArgsConstructor // Gera um construtor com todos os argumentos (Lombok)
@EqualsAndHashCode(of = "id") // Gera equals e hashCode baseado no campo 'id' (Lombok)
public class Resposta {

    @Id // Marca o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia de geração de ID (auto-incremento)
    private Long id;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private Boolean solucao = false; // Indica se esta resposta é a solução para o tópico

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com Topico (lazy loading)
    @JoinColumn(name = "topico_id") // Coluna de chave estrangeira na tabela 'respostas'
    private Topico topico;

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com Usuario (lazy loading)
    @JoinColumn(name = "autor_id") // Coluna de chave estrangeira na tabela 'respostas'
    private Usuario autor;

    // Construtor para criação de uma nova resposta
    public Resposta(String mensagem, Topico topico, Usuario autor) {
        this.mensagem = mensagem;
        this.dataCriacao = LocalDateTime.now(); // Define a data de criação automaticamente
        this.solucao = false; // Por padrão, não é a solução ao ser criada
        this.topico = topico;
        this.autor = autor;
    }

    // Método para marcar a resposta como solução
    public void marcarComoSolucao() {
        this.solucao = true;
    }

    // Método para atualizar a mensagem da resposta
    public void atualizarMensagem(String novaMensagem) {
        if (novaMensagem != null && !novaMensagem.isBlank()) {
            this.mensagem = novaMensagem;
        }
    }
}
