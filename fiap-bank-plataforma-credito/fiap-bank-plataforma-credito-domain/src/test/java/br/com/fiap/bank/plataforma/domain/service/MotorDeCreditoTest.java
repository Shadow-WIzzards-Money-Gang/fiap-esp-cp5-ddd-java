package br.com.fiap.bank.plataforma.domain.service;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.enums.TipoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.AnaliseRisco;
import br.com.fiap.bank.plataforma.domain.valueobject.ContaCliente;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.DecisaoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MotorDeCreditoTest {

    private static final Optional<ContaCliente> CONTA_ATIVA =
            Optional.of(new ContaCliente("12345-6", "0001", new BigDecimal("12500.00"), "ATIVA"));

    private final MotorDeCredito motor = new MotorDeCredito();

    @Test
    void reprovaQuandoHaRestricaoNoScr() {
        DecisaoCredito decisao = motor.avaliar(proposta("15000.00", 24, "7500.00"),
                new AnaliseRisco(true, 900, false, CONTA_ATIVA));

        assertThat(decisao.status()).isEqualTo(StatusSolicitacao.REPROVADO);
        assertThat(decisao.parecer()).contains("SCR");
    }

    @Test
    void reprovaQuandoScoreDoBureauEInsuficiente() {
        DecisaoCredito decisao = motor.avaliar(proposta("15000.00", 24, "7500.00"),
                new AnaliseRisco(false, 499, false, CONTA_ATIVA));

        assertThat(decisao.status()).isEqualTo(StatusSolicitacao.REPROVADO);
        assertThat(decisao.parecer()).contains("499");
    }

    @Test
    void encaminhaParaMesaQuandoClienteNaoTemContaAtivaNoCore() {
        DecisaoCredito semConta = motor.avaliar(proposta("15000.00", 24, "7500.00"),
                new AnaliseRisco(false, 805, false, Optional.empty()));
        DecisaoCredito contaEncerrada = motor.avaliar(proposta("15000.00", 24, "7500.00"),
                new AnaliseRisco(false, 805, false,
                        Optional.of(new ContaCliente("12345-6", "0001", BigDecimal.ZERO, "ENCERRADA"))));

        assertThat(semConta.status()).isEqualTo(StatusSolicitacao.EM_ANALISE);
        assertThat(contaEncerrada.status()).isEqualTo(StatusSolicitacao.EM_ANALISE);
    }

    @Test
    void encaminhaParaMesaQuandoHaHistoricoNegativoNoCrm() {
        DecisaoCredito decisao = motor.avaliar(proposta("15000.00", 24, "7500.00"),
                new AnaliseRisco(false, 805, true, CONTA_ATIVA));

        assertThat(decisao.status()).isEqualTo(StatusSolicitacao.EM_ANALISE);
        assertThat(decisao.parecer()).contains("CRM");
    }

    @Test
    void encaminhaParaMesaQuandoParcelaComprometeMaisDe30PorCentoDaRenda() {
        // 24000 / 12 = 2000 > 30% de 6000 (1800)
        DecisaoCredito decisao = motor.avaliar(proposta("24000.00", 12, "6000.00"),
                new AnaliseRisco(false, 805, false, CONTA_ATIVA));

        assertThat(decisao.status()).isEqualTo(StatusSolicitacao.EM_ANALISE);
        assertThat(decisao.parecer()).contains("2000.00");
    }

    @Test
    void aprovaQuandoTodasAsRegrasSaoAtendidas() {
        // 15000 / 24 = 625 <= 30% de 7500 (2250)
        DecisaoCredito decisao = motor.avaliar(proposta("15000.00", 24, "7500.00"),
                new AnaliseRisco(false, 805, false, CONTA_ATIVA));

        assertThat(decisao.status()).isEqualTo(StatusSolicitacao.APROVADO);
    }

    private static PropostaCredito proposta(String valor, int parcelas, String renda) {
        return new PropostaCredito(new Cpf("12345678900"), "Ana Silva", TipoCredito.EMPRESTIMO_PESSOAL,
                Dinheiro.de(valor), parcelas, Dinheiro.de(renda));
    }
}
