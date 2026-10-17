package br.com.fiap.bank.plataforma.domain.service;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;
import br.com.fiap.bank.plataforma.domain.valueobject.AnaliseRisco;
import br.com.fiap.bank.plataforma.domain.valueobject.DecisaoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;

import java.math.BigDecimal;

/**
 * Motor de regras de concessao. As regras sao avaliadas em ordem; a primeira que se aplica decide.
 */
public class MotorDeCredito {

    public static final int SCORE_MINIMO = 500;
    public static final BigDecimal COMPROMETIMENTO_MAXIMO_RENDA = new BigDecimal("0.30");

    private static final String ENCAMINHADO_MESA = "Encaminhado para a Mesa de Operacoes de Credito. ";

    public DecisaoCredito avaliar(PropostaCredito proposta, AnaliseRisco analise) {
        if (proposta == null || analise == null) {
            throw new ValorInvalidoException("Proposta e analise de risco sao obrigatorias.");
        }

        if (analise.restricaoScr()) {
            return reprovar("Solicitacao reprovada automaticamente devido a restricoes ativas no SCR (Banco Central).");
        }
        if (analise.scoreBureau() < SCORE_MINIMO) {
            return reprovar("Solicitacao reprovada automaticamente. Score no Bureau de Credito insuficiente ("
                    + analise.scoreBureau() + ").");
        }
        if (!analise.possuiContaAtiva()) {
            return encaminharParaMesa("Nao foi possivel confirmar uma conta ativa do cliente no Core Bancario.");
        }
        if (analise.historicoNegativoCrm()) {
            return encaminharParaMesa("Cliente possui historico de inadimplencia no CRM do FIAP Bank.");
        }

        Dinheiro parcelaEstimada = proposta.parcelaEstimada();
        if (parcelaEstimada.maiorQue(proposta.rendaMensal().multiplicar(COMPROMETIMENTO_MAXIMO_RENDA))) {
            return encaminharParaMesa("Parcela estimada (R$ " + parcelaEstimada
                    + ") compromete mais de 30% da renda informada.");
        }

        return new DecisaoCredito(StatusSolicitacao.APROVADO,
                "Solicitacao aprovada automaticamente pelo Motor de Credito FIAP Bank!");
    }

    private static DecisaoCredito reprovar(String parecer) {
        return new DecisaoCredito(StatusSolicitacao.REPROVADO, parecer);
    }

    private static DecisaoCredito encaminharParaMesa(String motivo) {
        return new DecisaoCredito(StatusSolicitacao.EM_ANALISE, ENCAMINHADO_MESA + motivo);
    }
}
