package com.alura.forumhub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Table(name = "roles") // Define o nome da tabela no banco de dados
@Entity(name = "Role") // Define o nome da entidade JPA
@Getter // Gera getters para todos os campos (Lombok)
@NoArgsConstructor // Gera um construtor sem argumentos (Lombok)
@AllArgsConstructor // Gera um construtor com todos os argumentos (Lombok)
@EqualsAndHashCode(of = "id") // Gera equals e hashCode baseado no campo 'id' (Lombok)
public class Role implements GrantedAuthority { // Implementa GrantedAuthority para Spring Security

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome; // Ex: "ROLE_USER", "ROLE_ADMIN"

    @Override
    public String getAuthority() {
        return nome; // O nome da role será a autoridade reconhecida pelo Spring Security
    }

    public Role(String nome) {
        this.nome = nome;
    }
}
