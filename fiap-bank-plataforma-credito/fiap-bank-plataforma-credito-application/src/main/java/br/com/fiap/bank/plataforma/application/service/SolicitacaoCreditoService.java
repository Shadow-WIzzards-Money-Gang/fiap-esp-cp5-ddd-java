package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.application.dto.request.AtualizarStatusRequest;
import br.com.fiap.bank.plataforma.application.dto.request.CriarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.application.dto.response.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.dto.response.SolicitacaoCreditoResponse;
import br.com.fiap.bank.plataforma.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.bank.plataforma.domain.gateway.BureauCreditoGateway;
import br.com.fiap.bank.plataforma.domain.gateway.CoreBancarioGateway;
import br.com.fiap.bank.plataforma.domain.gateway.NotificacaoGateway;
import br.com.fiap.bank.plataforma.domain.gateway.ScrGateway;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SolicitacaoCreditoService {

    private static final String STATUS_APROVADO = "APROVADO";

    private final SolicitacaoCreditoRepository solicitacaoCreditoRepository;
    private final ScrGateway scrGateway;
    private final BureauCreditoGateway bureauCreditoGateway;
    private final NotificacaoGateway notificacaoGateway;
    private final CoreBancarioGateway coreBancarioGateway;

    public SolicitacaoCreditoResponse solicitar(CriarSolicitacaoRequest request) {
        PropostaCredito proposta = TradutorExcecaoDominio.obter(() -> new PropostaCredito(
                new Cpf(request.cpfCliente()),
                request.nomeCliente(),
                request.tipoCredito(),
                new Dinheiro(request.valorSolicitado()),
                request.quantidadeParcelas(),
                new Dinheiro(request.rendaMensal())));

        SolicitacaoCredito solicitacao = SolicitacaoCredito.nova(proposta);
        boolean restricaoScr = scrGateway.possuiRestricao(proposta.cpf());
        int scoreBureau = bureauCreditoGateway.consultarScore(proposta.cpf());
        TradutorExcecaoDominio.executar(() -> solicitacao.avaliar(restricaoScr, scoreBureau));

        SolicitacaoCredito salva = solicitacaoCreditoRepository.salvar(solicitacao);
        notificacaoGateway.notificar(proposta.cpf(),
                "Sua solicitacao de credito #" + salva.getId() + " esta no status: " + salva.getStatus());

        return SolicitacaoCreditoResponse.de(salva);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoCreditoResponse> listar() {
        return solicitacaoCreditoRepository.listarTodas().stream().map(SolicitacaoCreditoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoCreditoResponse> listarPorCpf(String cpf) {
        Cpf cpfDoCliente = TradutorExcecaoDominio.obter(() -> new Cpf(cpf));
        return solicitacaoCreditoRepository.listarPorCpf(cpfDoCliente).stream()
                .map(SolicitacaoCreditoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolicitacaoCreditoResponse buscarPorId(Long id) {
        return SolicitacaoCreditoResponse.de(obterPorId(id));
    }

    public SolicitacaoCreditoResponse atualizarStatus(Long id, AtualizarStatusRequest request) {
        SolicitacaoCredito solicitacao = obterPorId(id);
        boolean aprovar = STATUS_APROVADO.equalsIgnoreCase(request.status());
        TradutorExcecaoDominio.executar(() -> {
            if (aprovar) {
                solicitacao.aprovar(request.parecer());
            } else {
                solicitacao.reprovar(request.parecer());
            }
        });

        SolicitacaoCredito salva = solicitacaoCreditoRepository.salvar(solicitacao);
        notificacaoGateway.notificar(salva.getProposta().cpf(),
                "Sua solicitacao de credito #" + salva.getId() + " foi " + salva.getStatus()
                        + " pela Mesa de Operacoes.");

        return SolicitacaoCreditoResponse.de(salva);
    }

    @Transactional(readOnly = true)
    public ContaClienteResponse buscarContaCliente(String cpf) {
        Cpf cpfDoCliente = TradutorExcecaoDominio.obter(() -> new Cpf(cpf));
        return TradutorExcecaoDominio.obter(() -> coreBancarioGateway.buscarConta(cpfDoCliente))
                .map(ContaClienteResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente com CPF " + cpfDoCliente + " nao possui conta no Core Bancario."));
    }

    private SolicitacaoCredito obterPorId(Long id) {
        return solicitacaoCreditoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao " + id + " nao encontrada."));
    }
}
