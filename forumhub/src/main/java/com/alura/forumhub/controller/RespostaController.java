package com.alura.forumhub.controller;

import com.alura.forumhub.dto.RespostaAtualizacaoDTO;
import com.alura.forumhub.dto.RespostaCadastroDTO;
import com.alura.forumhub.dto.RespostaDetalheDTO;
import com.alura.forumhub.dto.RespostaListagemDTO;
import com.alura.forumhub.model.Resposta;
import com.alura.forumhub.model.Topico;
import com.alura.forumhub.model.Usuario;
import com.alura.forumhub.repository.RespostaRepository;
import com.alura.forumhub.repository.TopicoRepository;
import com.alura.forumhub.repository.UsuarioRepository;
import com.alura.forumhub.infra.security.ValidacaoException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/respostas")
public class RespostaController {

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RespostaDetalheDTO> cadastrar(@RequestBody @Valid RespostaCadastroDTO dados,
                                                        UriComponentsBuilder uriBuilder) {
        Topico topico = topicoRepository.findById(dados.idTopico())
                .orElseThrow(() -> new ValidacaoException("Tópico não encontrado para a resposta."));
        Usuario autor = usuarioRepository.findById(dados.idAutor())
                .orElseThrow(() -> new ValidacaoException("Autor da resposta não encontrado."));

        if (respostaRepository.existsByTopicoIdAndMensagem(dados.idTopico(), dados.mensagem())) {
            throw new ValidacaoException("Já existe uma resposta com esta mensagem para este tópico."); // CORRIGIDO AQUI!
        }

        Resposta resposta = new Resposta(dados.mensagem(), topico, autor);
        respostaRepository.save(resposta);

        URI uri = uriBuilder.path("/respostas/{id}").buildAndExpand(resposta.getId()).toUri();
        return ResponseEntity.created(uri).body(new RespostaDetalheDTO(resposta));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<RespostaListagemDTO>> listar(@PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        Page<RespostaListagemDTO> page = respostaRepository.findAll(paginacao).map(RespostaListagemDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RespostaDetalheDTO> detalhar(@PathVariable Long id) {
        Optional<Resposta> respostaOptional = respostaRepository.findById(id);
        if (respostaOptional.isPresent()) {
            return ResponseEntity.ok(new RespostaDetalheDTO(respostaOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or @respostaRepository.findById(#id).get().getAutor().getEmail() == authentication.name")
    public ResponseEntity<RespostaDetalheDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid RespostaAtualizacaoDTO dados) {
        Optional<Resposta> respostaOptional = respostaRepository.findById(id);
        if (respostaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Resposta resposta = respostaOptional.get();
        resposta.atualizarMensagem(dados.mensagem());
        respostaRepository.save(resposta);
        return ResponseEntity.ok(new RespostaDetalheDTO(resposta));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') or @respostaRepository.findById(#id).get().getAutor().getEmail() == authentication.name")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (respostaRepository.existsById(id)) {
            respostaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/marcar-solucao")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')") // SIMPLIFICADO AQUI!
    public ResponseEntity<RespostaDetalheDTO> marcarComoSolucao(@PathVariable Long id) {
        Optional<Resposta> respostaOptional = respostaRepository.findById(id);
        if (respostaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Resposta resposta = respostaOptional.get();
        Topico topico = resposta.getTopico();

        if (topico.isFechado()) {
            throw new ValidacaoException("Não é possível marcar uma solução para um tópico fechado.");
        }

        if (respostaRepository.existsByTopicoIdAndSolucaoTrue(topico.getId())) {
            throw new ValidacaoException("Este tópico já possui uma solução marcada.");
        }

        resposta.marcarComoSolucao();
        topico.marcarComoSolucionado();
        respostaRepository.save(resposta);
        topicoRepository.save(topico);
        return ResponseEntity.ok(new RespostaDetalheDTO(resposta));
    }
}