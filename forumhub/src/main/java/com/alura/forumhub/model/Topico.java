package com.alura.forumhub.model;

import com.alura.forumhub.dto.TopicoAtualizacaoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "topicos") // Define o nome da tabela no banco de dados
@Entity(name = "Topico") // Define o nome da entidade JPA
@Getter // Gera getters para todos os campos (Lombok)
@NoArgsConstructor // Gera um construtor sem argumentos (Lombok)
@AllArgsConstructor // Gera um construtor com todos os argumentos (Lombok)
@EqualsAndHashCode(of = "id") // Gera equals e hashCode baseado no campo 'id' (Lombok)
public class Topico {

    @Id // Marca o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia de geração de ID (auto-incremento)
    private Long id;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataCriacao = LocalDateTime.now(); // Data de criação padrão para agora

    @Enumerated(EnumType.STRING) // Mapeia o enum como String no banco de dados
    private StatusTopico status = StatusTopico.NAO_RESPONDIDO; // Status padrão

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com Usuario (carregamento preguiçoso)
    @JoinColumn(name = "autor_id") // Coluna de chave estrangeira
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com Curso (carregamento preguiçoso)
    @JoinColumn(name = "curso_id") // Coluna de chave estrangeira
    private Curso curso;

    // Relacionamento um-para-muitos com Resposta (carregamento preguiçoso)
    // mappedBy indica o nome do campo na entidade Resposta que mapeia este lado do relacionamento
    // cascade = CascadeType.ALL significa que operações de persistência (salvar, atualizar, excluir)
    // em Topico serão propagadas para as Respostas associadas.
    // orphanRemoval = true garante que respostas que não estão mais associadas a um tópico sejam removidas.
    @OneToMany(mappedBy = "topico", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Resposta> respostas; // Usar java.util.List para evitar conflitos com lombok.experimental.Delegate

    // --- NOVOS CAMPOS E MÉTODOS ---
    // Indica se o tópico foi solucionado, com valor padrão FALSE no banco de dados
    @Column(nullable = false, columnDefinition = "boolean default false") // ADICIONADO: Define default no DB
    private boolean solucionado = false;

    // Indica se o tópico foi fechado, com valor padrão FALSE no banco de dados
    @Column(nullable = false, columnDefinition = "boolean default false") // ADICIONADO: Define default no DB
    private boolean fechado = false;

    // Construtor para criação de um novo tópico
    public Topico(String titulo, String mensagem, Usuario autor, Curso curso) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.autor = autor;
        this.curso = curso;
    }

    // Método para atualizar informações do tópico
    public void atualizarInformacoes(String titulo, String mensagem, Curso curso) {
        if (titulo != null && !titulo.isBlank()) {
            this.titulo = titulo;
        }
        if (mensagem != null && !mensagem.isBlank()) {
            this.mensagem = mensagem;
        }
        if (curso != null) {
            this.curso = curso;
        }
    }

    // Método para marcar o tópico como solucionado
    public void marcarComoSolucionado() {
        this.solucionado = true;
        this.status = StatusTopico.SOLUCIONADO; // Altera o status para SOLUCIONADO
    }

    // Método para fechar o tópico (não permite mais respostas ou alterações)
    public void fecharTopico() {
        this.fechado = true;
        // O status pode permanecer SOLUCIONADO ou ser alterado para FECHADO, dependendo da regra de negócio
        // Por exemplo, pode-se criar um novo StatusTopico.FECHADO
    }

    // Método para reabrir o tópico (apenas se estiver fechado e não solucionado)
    public void reabrirTopico() {
        if (this.fechado && !this.solucionado) {
            this.fechado = false;
            this.status = StatusTopico.NAO_RESPONDIDO; // Volta ao status inicial
        }
    }
}