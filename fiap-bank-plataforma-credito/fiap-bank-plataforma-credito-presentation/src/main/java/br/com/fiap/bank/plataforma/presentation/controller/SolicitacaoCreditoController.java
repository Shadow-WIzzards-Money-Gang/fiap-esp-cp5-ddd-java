package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.request.AtualizarStatusRequest;
import br.com.fiap.bank.plataforma.application.dto.request.CriarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.application.dto.response.SolicitacaoCreditoResponse;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitacoes")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SolicitacaoCreditoController {

    private final SolicitacaoCreditoService solicitacaoCreditoService;

    @PostMapping
    public ResponseEntity<SolicitacaoCreditoResponse> solicitar(@Valid @RequestBody CriarSolicitacaoRequest request) {
        SolicitacaoCreditoResponse solicitacao = solicitacaoCreditoService.solicitar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(solicitacao.id())
                .toUri();
        return ResponseEntity.created(location).body(solicitacao);
    }

    @GetMapping
    public ResponseEntity<List<SolicitacaoCreditoResponse>> listar(
            @RequestParam(name = "cpf", required = false) String cpf) {
        if (cpf != null && !cpf.isBlank()) {
            return ResponseEntity.ok(solicitacaoCreditoService.listarPorCpf(cpf));
        }
        return ResponseEntity.ok(solicitacaoCreditoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoCreditoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoCreditoService.buscarPorId(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SolicitacaoCreditoResponse> atualizarStatus(@PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(solicitacaoCreditoService.atualizarStatus(id, request));
    }
}
