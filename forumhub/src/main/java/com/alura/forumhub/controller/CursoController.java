package com.alura.forumhub.controller;

import com.alura.forumhub.dto.CursoCadastroDTO;
import com.alura.forumhub.dto.CursoDetalheDTO;
import com.alura.forumhub.dto.CursoListagemDTO;
import com.alura.forumhub.model.Curso;
import com.alura.forumhub.repository.CursoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importação adicionada
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/cursos") // Define o caminho base para os endpoints de curso
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository; // Injeta o repositório de Curso

    // Endpoint para cadastrar um novo curso
    // Apenas ADMIN pode cadastrar cursos
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Apenas ADMIN pode cadastrar
    public ResponseEntity<CursoDetalheDTO> cadastrar(@RequestBody @Valid CursoCadastroDTO dados,
                                                     UriComponentsBuilder uriBuilder) {
        // Cria o curso
        Curso curso = new Curso(dados.nome(), dados.categoria());
        cursoRepository.save(curso); // Salva o curso no banco de dados

        // Constrói a URI para o novo recurso criado
        URI uri = uriBuilder.path("/cursos/{id}").buildAndExpand(curso.getId()).toUri();
        // Retorna 201 Created com a URI e os detalhes do curso
        return ResponseEntity.created(uri).body(new CursoDetalheDTO(curso));
    }

    // Endpoint para listar todos os cursos (com paginação)
    // Qualquer usuário autenticado pode listar cursos
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')") // Permite USER e ADMIN
    public ResponseEntity<Page<CursoListagemDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        // Busca todos os cursos paginados e mapeia para CursoListagemDTO
        Page<CursoListagemDTO> page = cursoRepository.findAll(paginacao).map(CursoListagemDTO::new);
        return ResponseEntity.ok(page); // Retorna 200 OK com a página de cursos
    }

    // Endpoint para mostrar um curso específico por ID
    // Qualquer usuário autenticado pode detalhar um curso
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')") // Permite USER e ADMIN
    public ResponseEntity<CursoDetalheDTO> detalhar(@PathVariable Long id) {
        Optional<Curso> cursoOptional = cursoRepository.findById(id);
        if (cursoOptional.isPresent()) {
            // Se o curso for encontrado, retorna 200 OK com seus detalhes
            return ResponseEntity.ok(new CursoDetalheDTO(cursoOptional.get()));
        }
        // Se o curso não for encontrado, retorna 404 Not Found
        return ResponseEntity.notFound().build();
    }

    // Endpoint para atualizar um curso existente
    // Apenas ADMIN pode atualizar cursos
    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Apenas ADMIN pode atualizar
    public ResponseEntity<CursoDetalheDTO> atualizar(@PathVariable Long id,
                                                     @RequestBody @Valid CursoCadastroDTO dados) {
        Optional<Curso> cursoOptional = cursoRepository.findById(id);
        if (cursoOptional.isPresent()) {
            Curso curso = cursoOptional.get();
            // Atualiza as informações do curso
            if (dados.nome() != null) {
                curso.setNome(dados.nome());
            }
            if (dados.categoria() != null) {
                curso.setCategoria(dados.categoria());
            }
            cursoRepository.save(curso);
            return ResponseEntity.ok(new CursoDetalheDTO(curso));
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint para eliminar um curso
    // Apenas ADMIN pode eliminar cursos
    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Apenas ADMIN pode eliminar
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (cursoRepository.existsById(id)) {
            cursoRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content para exclusão bem-sucedida
        }
        return ResponseEntity.notFound().build(); // Retorna 404 Not Found se o curso não existir
    }
}