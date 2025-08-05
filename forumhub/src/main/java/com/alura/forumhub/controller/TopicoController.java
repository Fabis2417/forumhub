package com.alura.forumhub.controller;

import com.alura.forumhub.dto.TopicoAtualizacaoDTO;
import com.alura.forumhub.dto.TopicoCadastroDTO;
import com.alura.forumhub.dto.TopicoDetalheDTO;
import com.alura.forumhub.dto.TopicoListagemDTO;
import com.alura.forumhub.model.Curso;
import com.alura.forumhub.model.StatusTopico; // Importação adicionada
import com.alura.forumhub.model.Topico;
import com.alura.forumhub.model.Usuario;
import com.alura.forumhub.repository.CursoRepository;
import com.alura.forumhub.repository.TopicoRepository;
import com.alura.forumhub.repository.UsuarioRepository;
import com.alura.forumhub.infra.security.ValidacaoException; // Importação da nova exceção
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Importação adicionada
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/topicos") // Define o caminho base para os endpoints de tópico
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository; // Injeta o repositório de Topico

    @Autowired
    private UsuarioRepository usuarioRepository; // Injeta o repositório de Usuario

    @Autowired
    private CursoRepository cursoRepository; // Injeta o repositório de Curso

    // Endpoint para cadastrar um novo tópico
    // Apenas usuários autenticados (USER ou ADMIN) podem cadastrar tópicos
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')") // Permite USER e ADMIN
    public ResponseEntity<TopicoDetalheDTO> cadastrar(@RequestBody @Valid TopicoCadastroDTO dados,
                                                      UriComponentsBuilder uriBuilder) {
        // Verifica se já existe um tópico com o mesmo título e mensagem
        if (topicoRepository.existsByTituloAndMensagem(dados.titulo(), dados.mensagem())) {
            throw new ValidacaoException("Já existe um tópico com este título e mensagem."); // Lança ValidacaoException
        }

        // Busca o Autor (Usuario) e o Curso pelo ID
        Usuario autor = usuarioRepository.findById(dados.idAutor())
                .orElseThrow(() -> new ValidacaoException("Autor não encontrado")); // Lança ValidacaoException
        Curso curso = cursoRepository.findById(dados.idCurso())
                .orElseThrow(() -> new ValidacaoException("Curso não encontrado")); // Lança ValidacaoException

        // Cria o tópico com os objetos Usuario e Curso
        Topico topico = new Topico(dados.titulo(), dados.mensagem(), autor, curso);
        topicoRepository.save(topico); // Salva o tópico no banco de dados

        // Constrói a URI para o novo recurso criado
        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        // Retorna 201 Created com a URI e os detalhes do tópico
        return ResponseEntity.created(uri).body(new TopicoDetalheDTO(topico));
    }

    // Endpoint para listar todos os tópicos (com paginação)
    // Qualquer usuário autenticado pode listar tópicos
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')") // Permite USER e ADMIN
    public ResponseEntity<Page<TopicoListagemDTO>> listar(@PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        // Busca todos os tópicos paginados e mapeia para TopicoListagemDTO
        Page<TopicoListagemDTO> page = topicoRepository.findAll(paginacao).map(TopicoListagemDTO::new);
        return ResponseEntity.ok(page); // Retorna 200 OK com a página de tópicos
    }

    // Endpoint para mostrar um tópico específico por ID
    // Qualquer usuário autenticado pode detalhar um tópico
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')") // Permite USER e ADMIN
    public ResponseEntity<TopicoDetalheDTO> detalhar(@PathVariable Long id) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isPresent()) {
            // Se o tópico for encontrado, retorna 200 OK com seus detalhes
            return ResponseEntity.ok(new TopicoDetalheDTO(topicoOptional.get()));
        }
        // Se o tópico não for encontrado, retorna 404 Not Found
        return ResponseEntity.notFound().build();
    }

    // Endpoint para atualizar um tópico existente
    // Apenas ADMIN ou o próprio autor do tópico pode atualizar
    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or @topicoRepository.findById(#id).get().getAutor().getEmail() == authentication.name")
    public ResponseEntity<TopicoDetalheDTO> atualizar(@PathVariable Long id,
                                                      @RequestBody @Valid TopicoAtualizacaoDTO dados,
                                                      Authentication authentication) { // Adicionado Authentication
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isEmpty()) { // Usar isEmpty() é mais idiomático
            return ResponseEntity.notFound().build();
        }
        Topico topico = topicoOptional.get();

        // Lógica de autorização já no @PreAuthorize, mas manter para clareza
        // if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) &&
        //     !topico.getAutor().getEmail().equals(authentication.getName())) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        // }

        Curso curso = null;
        if (dados.idCurso() != null) {
            curso = cursoRepository.findById(dados.idCurso())
                    .orElseThrow(() -> new ValidacaoException("Curso não encontrado para atualização")); // Lança ValidacaoException
        }

        topico.atualizarInformacoes(dados.titulo(), dados.mensagem(), curso);
        topicoRepository.save(topico);
        return ResponseEntity.ok(new TopicoDetalheDTO(topico));
    }

    // Endpoint para eliminar um tópico
    // Apenas ADMIN pode eliminar tópicos
    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Apenas ADMIN pode eliminar
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (topicoRepository.existsById(id)) {
            topicoRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content para exclusão bem-sucedida
        }
        return ResponseEntity.notFound().build(); // Retorna 404 Not Found se o tópico não existir
    }

    // --- NOVOS ENDPOINTS PARA SOLUCIONAR E FECHAR TÓPICO ---

    // Endpoint para marcar um tópico como solucionado
    // Apenas ADMIN ou o próprio autor do tópico pode marcar como solucionado
    @PatchMapping("/{id}/solucionar")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or @topicoRepository.findById(#id).get().getAutor().getEmail() == authentication.name")
    public ResponseEntity<TopicoDetalheDTO> marcarComoSolucionado(@PathVariable Long id,
                                                                  Authentication authentication) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Topico topico = topicoOptional.get();

        if (topico.isSolucionado()) {
            throw new ValidacaoException("Tópico já está solucionado."); // Lança ValidacaoException
        }
        if (topico.isFechado()) {
            throw new ValidacaoException("Tópico está fechado e não pode ser solucionado."); // Lança ValidacaoException
        }

        topico.marcarComoSolucionado(); // Chama o novo método na entidade Topico
        topicoRepository.save(topico);
        return ResponseEntity.ok(new TopicoDetalheDTO(topico));
    }

    // Endpoint para fechar um tópico
    // Apenas ADMIN ou o próprio autor do tópico pode fechar
    @PatchMapping("/{id}/fechar")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or @topicoRepository.findById(#id).get().getAutor().getEmail() == authentication.name")
    public ResponseEntity<TopicoDetalheDTO> fecharTopico(@PathVariable Long id,
                                                         Authentication authentication) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Topico topico = topicoOptional.get();

        if (topico.isFechado()) {
            throw new ValidacaoException("Tópico já está fechado."); // Lança ValidacaoException
        }

        topico.fecharTopico(); // Chama o novo método na entidade Topico
        topicoRepository.save(topico);
        return ResponseEntity.ok(new TopicoDetalheDTO(topico));
    }

    // --- NOVO ENDPOINT PARA REABRIR TÓPICO ---

    // Endpoint para reabrir um tópico
    // Apenas ADMIN ou o próprio autor do tópico pode reabrir
    @PatchMapping("/{id}/reabrir")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or @topicoRepository.findById(#id).get().getAutor().getEmail() == authentication.name")
    public ResponseEntity<TopicoDetalheDTO> reabrirTopico(@PathVariable Long id,
                                                          Authentication authentication) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Topico topico = topicoOptional.get();

        // Verifica se o tópico pode ser reaberto (ex: se está fechado e não solucionado)
        if (!topico.isFechado()) { // Se não estiver fechado, não pode ser reaberto
            throw new ValidacaoException("Tópico não está fechado para ser reaberto.");
        }
        if (topico.isSolucionado()) { // Se estiver solucionado, não pode ser reaberto
            throw new ValidacaoException("Tópico está solucionado e não pode ser reaberto.");
        }

        topico.reabrirTopico(); // Chama o novo método na entidade Topico
        topicoRepository.save(topico);
        return ResponseEntity.ok(new TopicoDetalheDTO(topico));
    }
}
