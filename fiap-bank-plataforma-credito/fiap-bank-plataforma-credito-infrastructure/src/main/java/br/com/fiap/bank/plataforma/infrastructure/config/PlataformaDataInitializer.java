package br.com.fiap.bank.plataforma.infrastructure.config;

import br.com.fiap.bank.plataforma.domain.gateway.BureauCreditoGateway;
import br.com.fiap.bank.plataforma.domain.gateway.ScrGateway;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga inicial da Plataforma de Credito. Os CPFs, nomes e rendas devem ficar consistentes com o
 * Core Bancario. A avaliacao usa apenas SCR e Bureau, entao o resultado nao depende do Core estar no ar.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlataformaDataInitializer implements CommandLineRunner {

    private final SolicitacaoCreditoRepository solicitacaoCreditoRepository;
    private final ScrGateway scrGateway;
    private final BureauCreditoGateway bureauCreditoGateway;

    @Override
    public void run(String... args) {
        if (!solicitacaoCreditoRepository.listarTodas().isEmpty()) {
            log.info("Carga inicial: solicitacoes ja existentes, nada a fazer.");
            return;
        }
        registrar("12345678900", "Ana Silva", "EMPRESTIMO_PESSOAL", "15000.00", 24, "7500.00");
        registrar("98765432100", "Carlos Eduardo Oliveira", "CREDITO_IMOBILIARIO", "350000.00", 180, "12000.00");
        registrar("55544433399", "Mariana Souza", "CREDITO_AUTOMOTIVO", "45000.00", 48, "4200.00");
    }

    private void registrar(String cpf, String nome, String tipoCredito, String valor, int parcelas, String renda) {
        PropostaCredito proposta = new PropostaCredito(new Cpf(cpf), nome, tipoCredito, Dinheiro.de(valor), parcelas,
                Dinheiro.de(renda));
        SolicitacaoCredito solicitacao = SolicitacaoCredito.nova(proposta);
        solicitacao.avaliar(scrGateway.possuiRestricao(proposta.cpf()), bureauCreditoGateway.consultarScore(proposta.cpf()));

        SolicitacaoCredito salva = solicitacaoCreditoRepository.salvar(solicitacao);
        log.info("Carga inicial: solicitacao #{} de {} (CPF {}) registrada com status {}.",
                salva.getId(), nome, cpf, salva.getStatus());
    }
}
