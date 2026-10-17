package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.exception.DominioException;
import br.com.fiap.bank.plataforma.domain.exception.TransicaoStatusInvalidaException;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agregado raiz da esteira de credito. O status so muda pelos metodos de negocio
 * {@link #avaliar(boolean, int)}, {@link #aprovar(String)} e {@link #reprovar(String)}.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitacaoCredito {

    public static final int SCORE_MINIMO = 500;
    public static final BigDecimal COMPROMETIMENTO_MAXIMO_RENDA = new BigDecimal("0.30");

    private static final int SCORE_BUREAU_MINIMO = 0;
    private static final int SCORE_BUREAU_MAXIMO = 1000;
    private static final String PARECER_PADRAO_APROVACAO = "OK";
    private static final String MOTIVO_PADRAO_REPROVACAO = "Inconformidade nos criterios de risco";

    /** Nulo ate a solicitacao ser persistida pela primeira vez. */
    @EqualsAndHashCode.Include
    private final Long id;
    private final PropostaCredito proposta;
    private StatusSolicitacao status;
    private String parecer;
    private boolean restricaoScr;
    /** Nulo enquanto a solicitacao nao foi avaliada pelo motor de credito. */
    private Integer scoreBureau;
    private final LocalDateTime dataSolicitacao;
    private LocalDateTime dataAtualizacao;

    /**
     * Abre uma solicitacao ainda nao avaliada. O status definitivo vem de {@link #avaliar(boolean, int)}.
     */
    public static SolicitacaoCredito nova(PropostaCredito proposta) {
        LocalDateTime agora = LocalDateTime.now();
        return reconstituir(null, proposta, StatusSolicitacao.EM_ANALISE, null, false, null, agora, agora);
    }

    /**
     * Recria uma solicitacao ja existente (ex.: vinda da persistencia), preservando seu estado.
     */
    public static SolicitacaoCredito reconstituir(Long id, PropostaCredito proposta, StatusSolicitacao status,
                                                  String parecer, boolean restricaoScr, Integer scoreBureau,
                                                  LocalDateTime dataSolicitacao, LocalDateTime dataAtualizacao) {
        if (proposta == null) {
            throw new ValorInvalidoException("Proposta de credito e obrigatoria.");
        }
        if (status == null) {
            throw new ValorInvalidoException("Status da solicitacao e obrigatorio.");
        }
        if (dataSolicitacao == null) {
            throw new ValorInvalidoException("Data da solicitacao e obrigatoria.");
        }
        return new SolicitacaoCredito(id, proposta, status, parecer, restricaoScr, scoreBureau,
                dataSolicitacao, dataAtualizacao);
    }

    /**
     * Motor de credito. As regras sao avaliadas em ordem; a primeira que se aplica decide:
     * restricao no SCR, score do Bureau abaixo do minimo, parcela acima de 30% da renda (Mesa) e aprovacao.
     */
    public void avaliar(boolean restricaoScr, int scoreBureau) {
        if (foiAvaliada()) {
            throw new DominioException("Solicitacao " + id + " ja foi avaliada pelo motor de credito.");
        }
        if (scoreBureau < SCORE_BUREAU_MINIMO || scoreBureau > SCORE_BUREAU_MAXIMO) {
            throw new ValorInvalidoException("Score do Bureau deve estar entre " + SCORE_BUREAU_MINIMO + " e "
                    + SCORE_BUREAU_MAXIMO + ".");
        }
        this.restricaoScr = restricaoScr;
        this.scoreBureau = scoreBureau;

        if (restricaoScr) {
            decidir(StatusSolicitacao.REPROVADO,
                    "Solicitacao reprovada automaticamente devido a restricoes ativas no SCR (Banco Central).");
            return;
        }
        if (scoreBureau < SCORE_MINIMO) {
            decidir(StatusSolicitacao.REPROVADO,
                    "Solicitacao reprovada automaticamente. Score no Bureau de Credito insuficiente ("
                            + scoreBureau + ").");
            return;
        }
        Dinheiro parcelaEstimada = proposta.parcelaEstimada();
        if (parcelaEstimada.maiorQue(proposta.rendaMensal().multiplicar(COMPROMETIMENTO_MAXIMO_RENDA))) {
            decidir(StatusSolicitacao.EM_ANALISE, "Encaminhado para a Mesa de Operacoes de Credito. Parcela estimada (R$ "
                    + parcelaEstimada + ") compromete mais de 30% da renda informada.");
            return;
        }
        decidir(StatusSolicitacao.APROVADO, "Solicitacao aprovada automaticamente pelo Motor de Credito FIAP Bank!");
    }

    /**
     * Decisao do operador da Mesa. So vale para solicitacoes em analise.
     */
    public void aprovar(String parecer) {
        decidirPelaMesa(StatusSolicitacao.APROVADO, "Aprovado pela Mesa de Operacoes: "
                + textoOuPadrao(parecer, PARECER_PADRAO_APROVACAO));
    }

    /**
     * Decisao do operador da Mesa. So vale para solicitacoes em analise.
     */
    public void reprovar(String motivo) {
        decidirPelaMesa(StatusSolicitacao.REPROVADO, "Reprovado pela Mesa de Operacoes. Motivo: "
                + textoOuPadrao(motivo, MOTIVO_PADRAO_REPROVACAO));
    }

    public boolean foiAvaliada() {
        return scoreBureau != null;
    }

    /**
     * Score de credito exposto aos front-ends. Como no sistema legado, e o proprio score do Bureau.
     */
    public Integer getScoreCredito() {
        return scoreBureau;
    }

    private void decidirPelaMesa(StatusSolicitacao novoStatus, String novoParecer) {
        if (!foiAvaliada() || status != StatusSolicitacao.EM_ANALISE) {
            throw new TransicaoStatusInvalidaException(id, status, novoStatus);
        }
        decidir(novoStatus, novoParecer);
    }

    private void decidir(StatusSolicitacao novoStatus, String novoParecer) {
        this.status = novoStatus;
        this.parecer = novoParecer;
        this.dataAtualizacao = LocalDateTime.now();
    }

    private static String textoOuPadrao(String texto, String padrao) {
        return texto == null || texto.isBlank() ? padrao : texto.trim();
    }
}
