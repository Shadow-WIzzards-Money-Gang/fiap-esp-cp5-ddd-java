package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.enums.TipoCredito;
import br.com.fiap.bank.plataforma.domain.exception.TransicaoStatusInvalidaException;
import br.com.fiap.bank.plataforma.domain.valueobject.AnaliseRisco;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.DecisaoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitacaoCreditoTest {

    @Test
    void registraComStatusEParecerDaDecisaoEIndicadoresDaAnalise() {
        SolicitacaoCredito solicitacao = registrar(StatusSolicitacao.EM_ANALISE);

        assertThat(solicitacao.getId()).isNull();
        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.EM_ANALISE);
        assertThat(solicitacao.getParecer()).isEqualTo("Parecer do motor");
        assertThat(solicitacao.getScoreBureau()).isEqualTo(805);
        assertThat(solicitacao.isRestricaoScr()).isFalse();
        assertThat(solicitacao.getDataSolicitacao()).isNotNull();
    }

    @Test
    void mesaAprovaSolicitacaoEmAnalise() {
        SolicitacaoCredito solicitacao = registrar(StatusSolicitacao.EM_ANALISE);

        solicitacao.aprovarPelaMesa("Renda comprovada");

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.APROVADO);
        assertThat(solicitacao.getParecer()).isEqualTo("Aprovado pela Mesa de Operacoes: Renda comprovada");
        assertThat(solicitacao.estaAprovada()).isTrue();
    }

    @Test
    void mesaReprovaSolicitacaoEmAnaliseComMotivoPadraoQuandoNaoInformado() {
        SolicitacaoCredito solicitacao = registrar(StatusSolicitacao.EM_ANALISE);

        solicitacao.reprovarPelaMesa(" ");

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.REPROVADO);
        assertThat(solicitacao.getParecer())
                .isEqualTo("Reprovado pela Mesa de Operacoes. Motivo: Inconformidade nos criterios de risco");
    }

    @Test
    void mesaNaoPodeDecidirSolicitacaoJaDecidida() {
        SolicitacaoCredito reprovada = registrar(StatusSolicitacao.REPROVADO);
        SolicitacaoCredito aprovada = registrar(StatusSolicitacao.APROVADO);

        assertThatThrownBy(() -> reprovada.aprovarPelaMesa("OK"))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
        assertThatThrownBy(() -> aprovada.reprovarPelaMesa("Mudei de ideia"))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    private static SolicitacaoCredito registrar(StatusSolicitacao status) {
        PropostaCredito proposta = new PropostaCredito(new Cpf("123.456.789-00"), "Ana Silva",
                TipoCredito.EMPRESTIMO_PESSOAL, Dinheiro.de("15000.00"), 24, Dinheiro.de("7500.00"));
        return SolicitacaoCredito.registrar(proposta, new AnaliseRisco(false, 805, false, Optional.empty()),
                new DecisaoCredito(status, "Parecer do motor"));
    }
}
