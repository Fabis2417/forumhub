package com.alura.forumhub.controller;

import com.alura.forumhub.dto.UsuarioCadastroDTO;
import com.alura.forumhub.dto.UsuarioDetalheDTO;
import com.alura.forumhub.dto.UsuarioListagemDTO;
import com.alura.forumhub.model.Role;
import com.alura.forumhub.model.Usuario;
import com.alura.forumhub.repository.UsuarioRepository;
import com.alura.forumhub.repository.RoleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<UsuarioDetalheDTO> cadastrar(@RequestBody @Valid UsuarioCadastroDTO dados,
                                                       UriComponentsBuilder uriBuilder) {
        if (usuarioRepository.findByEmail(dados.email()) != null) {
            return ResponseEntity.badRequest().body(null); // Retorna 400 se o email já existe
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        Usuario usuario = new Usuario(dados.nome(), dados.email(), senhaCriptografada);

        // --- LÓGICA PARA ATRIBUIÇÃO DE ROLE ---
        Role roleParaAtribuir;
        if (dados.role() != null && dados.role().equalsIgnoreCase("ROLE_ADMIN")) {
            roleParaAtribuir = roleRepository.findByNome("ROLE_ADMIN");
            if (roleParaAtribuir == null) {
                roleParaAtribuir = new Role("ROLE_ADMIN");
                roleRepository.save(roleParaAtribuir); // Salva a role ADMIN se ainda não existir
            }
        } else {
            roleParaAtribuir = roleRepository.findByNome("ROLE_USER");
            if (roleParaAtribuir == null) {
                roleParaAtribuir = new Role("ROLE_USER");
                roleRepository.save(roleParaAtribuir); // Salva a role USER se ainda não existir
            }
        }
        usuario.addRole(roleParaAtribuir); // Atribui a role ao novo utilizador
        // --- FIM DA LÓGICA DE ATRIBUIÇÃO DE ROLE ---

        usuarioRepository.save(usuario);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioDetalheDTO(usuario));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioListagemDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        Page<UsuarioListagemDTO> page = usuarioRepository.findAll(paginacao).map(UsuarioListagemDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDetalheDTO> detalhar(@PathVariable Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(new UsuarioDetalheDTO(usuarioOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }
}
