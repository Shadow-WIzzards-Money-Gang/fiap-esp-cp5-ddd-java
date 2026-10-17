package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.exception.DominioException;
import br.com.fiap.bank.plataforma.domain.exception.TransicaoStatusInvalidaException;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitacaoCreditoTest {

    @Test
    void novaSolicitacaoAindaNaoFoiAvaliada() {
        SolicitacaoCredito solicitacao = SolicitacaoCredito.nova(proposta("15000.00", 24, "7500.00"));

        assertThat(solicitacao.getId()).isNull();
        assertThat(solicitacao.foiAvaliada()).isFalse();
        assertThat(solicitacao.getScoreBureau()).isNull();
        assertThat(solicitacao.getDataSolicitacao()).isNotNull();
    }

    @Test
    void reprovaQuandoHaRestricaoNoScr() {
        SolicitacaoCredito solicitacao = avaliada("15000.00", 24, "7500.00", true, 900);

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.REPROVADO);
        assertThat(solicitacao.getParecer()).contains("SCR");
        assertThat(solicitacao.isRestricaoScr()).isTrue();
    }

    @Test
    void reprovaQuandoScoreDoBureauEInsuficiente() {
        SolicitacaoCredito solicitacao = avaliada("15000.00", 24, "7500.00", false, 499);

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.REPROVADO);
        assertThat(solicitacao.getParecer()).contains("499");
    }

    @Test
    void encaminhaParaMesaQuandoParcelaComprometeMaisDe30PorCentoDaRenda() {
        // 24000 / 12 = 2000 > 30% de 6000 (1800)
        SolicitacaoCredito solicitacao = avaliada("24000.00", 12, "6000.00", false, 805);

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.EM_ANALISE);
        assertThat(solicitacao.getParecer()).contains("2000.00");
    }

    @Test
    void aprovaQuandoParcelaEExatamente30PorCentoDaRenda() {
        // 21600 / 12 = 1800 = 30% de 6000
        assertThat(avaliada("21600.00", 12, "6000.00", false, 805).getStatus())
                .isEqualTo(StatusSolicitacao.APROVADO);
    }

    @Test
    void aprovaQuandoTodasAsRegrasSaoAtendidas() {
        // 15000 / 24 = 625 <= 30% de 7500 (2250)
        SolicitacaoCredito solicitacao = avaliada("15000.00", 24, "7500.00", false, 805);

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.APROVADO);
        assertThat(solicitacao.getScoreBureau()).isEqualTo(805);
        assertThat(solicitacao.getScoreCredito()).isEqualTo(805);
    }

    @Test
    void naoPodeSerAvaliadaDuasVezes() {
        SolicitacaoCredito solicitacao = avaliada("15000.00", 24, "7500.00", false, 805);

        assertThatThrownBy(() -> solicitacao.avaliar(false, 805)).isInstanceOf(DominioException.class);
    }

    @Test
    void rejeitaScoreForaDaFaixa() {
        SolicitacaoCredito solicitacao = SolicitacaoCredito.nova(proposta("15000.00", 24, "7500.00"));

        assertThatThrownBy(() -> solicitacao.avaliar(false, 1001)).isInstanceOf(ValorInvalidoException.class);
        assertThatThrownBy(() -> solicitacao.avaliar(false, -1)).isInstanceOf(ValorInvalidoException.class);
        assertThat(solicitacao.foiAvaliada()).isFalse();
    }

    @Test
    void mesaAprovaSolicitacaoEmAnalise() {
        SolicitacaoCredito solicitacao = emAnalise();

        solicitacao.aprovar("Renda comprovada");

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.APROVADO);
        assertThat(solicitacao.getParecer()).isEqualTo("Aprovado pela Mesa de Operacoes: Renda comprovada");
    }

    @Test
    void mesaReprovaSolicitacaoEmAnaliseComMotivoPadraoQuandoNaoInformado() {
        SolicitacaoCredito solicitacao = emAnalise();

        solicitacao.reprovar(" ");

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.REPROVADO);
        assertThat(solicitacao.getParecer())
                .isEqualTo("Reprovado pela Mesa de Operacoes. Motivo: Inconformidade nos criterios de risco");
    }

    @ParameterizedTest
    @EnumSource(value = StatusSolicitacao.class, names = "EM_ANALISE", mode = EnumSource.Mode.EXCLUDE)
    void mesaSoDecideSolicitacoesEmAnalise(StatusSolicitacao status) {
        SolicitacaoCredito solicitacao = SolicitacaoCredito.reconstituir(1L, proposta("15000.00", 24, "7500.00"),
                status, "Parecer", false, 805, LocalDateTime.now(), LocalDateTime.now());

        assertThatThrownBy(() -> solicitacao.aprovar("OK")).isInstanceOf(TransicaoStatusInvalidaException.class);
        assertThatThrownBy(() -> solicitacao.reprovar("Motivo")).isInstanceOf(TransicaoStatusInvalidaException.class);
        assertThat(solicitacao.getStatus()).isEqualTo(status);
    }

    @Test
    void mesaNaoDecideSolicitacaoAindaNaoAvaliada() {
        SolicitacaoCredito solicitacao = SolicitacaoCredito.nova(proposta("15000.00", 24, "7500.00"));

        assertThatThrownBy(() -> solicitacao.aprovar("OK")).isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    private static SolicitacaoCredito emAnalise() {
        return avaliada("24000.00", 12, "6000.00", false, 805);
    }

    private static SolicitacaoCredito avaliada(String valor, int parcelas, String renda, boolean restricaoScr,
                                               int scoreBureau) {
        SolicitacaoCredito solicitacao = SolicitacaoCredito.nova(proposta(valor, parcelas, renda));
        solicitacao.avaliar(restricaoScr, scoreBureau);
        return solicitacao;
    }

    private static PropostaCredito proposta(String valor, int parcelas, String renda) {
        return new PropostaCredito(new Cpf("123.456.789-00"), "Ana Silva", "EMPRÉSTIMO PESSOAL",
                Dinheiro.de(valor), parcelas, Dinheiro.de(renda));
    }
}
