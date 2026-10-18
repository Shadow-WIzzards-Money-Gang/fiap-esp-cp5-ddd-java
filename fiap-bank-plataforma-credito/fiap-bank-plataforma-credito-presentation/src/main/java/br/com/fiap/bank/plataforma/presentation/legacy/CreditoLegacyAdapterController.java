package br.com.fiap.bank.plataforma.presentation.legacy;

import br.com.fiap.bank.plataforma.application.dto.request.AtualizarStatusRequest;
import br.com.fiap.bank.plataforma.application.dto.request.CriarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.application.dto.response.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.dto.response.SolicitacaoCreditoResponse;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import br.com.fiap.bank.plataforma.presentation.legacy.dto.AprovarLegacyRequest;
import br.com.fiap.bank.plataforma.presentation.legacy.dto.CpfLegacyRequest;
import br.com.fiap.bank.plataforma.presentation.legacy.dto.IdLegacyRequest;
import br.com.fiap.bank.plataforma.presentation.legacy.dto.ReprovarLegacyRequest;
import br.com.fiap.bank.plataforma.presentation.legacy.dto.SolicitarCreditoLegacyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Compatibilidade com o Internet Banking (Angular) e a Mesa de Operacoes (Thymeleaf), que ainda
 * consomem as rotas nao-RESTful originais. Equivalente RESTful: {@code /api/v1/solicitacoes} e
 * {@code /api/v1/clientes/{cpf}/conta}. O adapter nao tem logica propria: so traduz o payload
 * legado e delega para o mesmo {@link SolicitacaoCreditoService} usado pelos controllers REST.
 */
@Deprecated
@RestController
@RequestMapping("/api/credito")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CreditoLegacyAdapterController {

    private final SolicitacaoCreditoService solicitacaoCreditoService;

    /** @deprecated use {@code POST /api/v1/solicitacoes} */
    @Deprecated
    @PostMapping("/solicitarCredito")
    public ResponseEntity<SolicitacaoCreditoResponse> solicitarCredito(
            @RequestBody SolicitarCreditoLegacyRequest request) {
        CriarSolicitacaoRequest novo = new CriarSolicitacaoRequest(request.cpfCliente(), request.nomeCliente(),
                request.tipoCredito(), request.valorSolicitado(), request.quantidadeParcelas(),
                request.rendaMensal());
        return ResponseEntity.ok(solicitacaoCreditoService.solicitar(novo));
    }

    /** @deprecated use {@code GET /api/v1/solicitacoes?cpf={cpf}} */
    @Deprecated
    @PostMapping("/consultarStatusCredito")
    public ResponseEntity<List<SolicitacaoCreditoResponse>> consultarStatusCredito(
            @RequestBody CpfLegacyRequest request) {
        return ResponseEntity.ok(solicitacaoCreditoService.listarPorCpf(request.cpfCliente()));
    }

    /**
     * Sem conta ou com o Core fora do ar, responde 200 com {@code statusConta} SEM_CONTA ou INDISPONIVEL,
     * porque o Internet Banking nao trata erro nesta chamada.
     *
     * @deprecated use {@code GET /api/v1/clientes/{cpf}/conta}
     */
    @Deprecated
    @PostMapping("/consultarClienteCore")
    public ResponseEntity<ContaClienteResponse> consultarClienteCore(@RequestBody CpfLegacyRequest request) {
        return ResponseEntity.ok(solicitacaoCreditoService.consultarSituacaoConta(request.cpfCliente()));
    }

    /** @deprecated use {@code GET /api/v1/solicitacoes} */
    @Deprecated
    @PostMapping("/listarTodasSolicitacoes")
    public ResponseEntity<List<SolicitacaoCreditoResponse>> listarTodasSolicitacoes() {
        return ResponseEntity.ok(solicitacaoCreditoService.listar());
    }

    /** @deprecated use {@code GET /api/v1/solicitacoes/{id}} */
    @Deprecated
    @PostMapping("/buscarSolicitacaoPorId")
    public ResponseEntity<SolicitacaoCreditoResponse> buscarSolicitacaoPorId(@RequestBody IdLegacyRequest request) {
        return ResponseEntity.ok(solicitacaoCreditoService.buscarPorId(request.solicitacaoId()));
    }

    /** @deprecated use {@code PUT /api/v1/solicitacoes/{id}/status} */
    @Deprecated
    @PostMapping("/aprovarSolicitacao")
    public ResponseEntity<SolicitacaoCreditoResponse> aprovarSolicitacao(@RequestBody AprovarLegacyRequest request) {
        AtualizarStatusRequest novo = new AtualizarStatusRequest("APROVADO", request.parecerOperador());
        return ResponseEntity.ok(solicitacaoCreditoService.atualizarStatus(request.solicitacaoId(), novo));
    }

    /** @deprecated use {@code PUT /api/v1/solicitacoes/{id}/status} */
    @Deprecated
    @PostMapping("/reprovarSolicitacao")
    public ResponseEntity<SolicitacaoCreditoResponse> reprovarSolicitacao(@RequestBody ReprovarLegacyRequest request) {
        AtualizarStatusRequest novo = new AtualizarStatusRequest("REPROVADO", request.motivoReprovacao());
        return ResponseEntity.ok(solicitacaoCreditoService.atualizarStatus(request.solicitacaoId(), novo));
    }
}
