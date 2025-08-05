package com.alura.forumhub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Importação adicionada

@Table(name = "cursos") // Define o nome da tabela no banco de dados
@Entity(name = "Curso") // Define o nome da entidade JPA
@Getter // Gera getters para todos os campos (Lombok)
@Setter // Gera setters para todos os campos (Lombok) - ADICIONADO
@NoArgsConstructor // Gera um construtor sem argumentos (Lombok)
@AllArgsConstructor // Gera um construtor com todos os argumentos (Lombok)
@EqualsAndHashCode(of = "id") // Gera equals e hashCode baseado no campo 'id' (Lombok)
public class Curso {

    @Id // Marca o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia de geração de ID (auto-incremento)
    private Long id;
    private String nome;
    private String categoria; // Ex: "Programação", "Data Science", "Design"

    // Construtor para criação de um novo curso
    public Curso(String nome, String categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }
}