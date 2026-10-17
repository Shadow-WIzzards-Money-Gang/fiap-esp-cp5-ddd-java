package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.exception.TransicaoStatusInvalidaException;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;
import br.com.fiap.bank.plataforma.domain.valueobject.AnaliseRisco;
import br.com.fiap.bank.plataforma.domain.valueobject.DecisaoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Agregado raiz da esteira de credito. O status so muda pelos metodos de negocio.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitacaoCredito {

    private static final String PARECER_PADRAO_APROVACAO = "OK";
    private static final String MOTIVO_PADRAO_REPROVACAO = "Inconformidade nos criterios de risco";

    /** Nulo ate a solicitacao ser persistida pela primeira vez. */
    @EqualsAndHashCode.Include
    private final Long id;
    private final PropostaCredito proposta;
    private StatusSolicitacao status;
    private String parecer;
    private final boolean restricaoScr;
    private final int scoreBureau;
    private final LocalDateTime dataSolicitacao;
    private LocalDateTime dataAtualizacao;

    /**
     * Registra uma nova solicitacao com o status e o parecer decididos pelo motor de credito.
     */
    public static SolicitacaoCredito registrar(PropostaCredito proposta, AnaliseRisco analise,
                                               DecisaoCredito decisao) {
        if (analise == null) {
            throw new ValorInvalidoException("Analise de risco e obrigatoria.");
        }
        if (decisao == null) {
            throw new ValorInvalidoException("Decisao do motor de credito e obrigatoria.");
        }
        LocalDateTime agora = LocalDateTime.now();
        return reconstituir(null, proposta, decisao.status(), decisao.parecer(), analise.restricaoScr(),
                analise.scoreBureau(), agora, agora);
    }

    /**
     * Recria uma solicitacao ja existente (ex.: vinda da persistencia), preservando seu estado.
     */
    public static SolicitacaoCredito reconstituir(Long id, PropostaCredito proposta, StatusSolicitacao status,
                                                  String parecer, boolean restricaoScr, int scoreBureau,
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

    public void aprovarPelaMesa(String parecerOperador) {
        decidirPelaMesa(StatusSolicitacao.APROVADO, "Aprovado pela Mesa de Operacoes: "
                + textoOuPadrao(parecerOperador, PARECER_PADRAO_APROVACAO));
    }

    public void reprovarPelaMesa(String motivoReprovacao) {
        decidirPelaMesa(StatusSolicitacao.REPROVADO, "Reprovado pela Mesa de Operacoes. Motivo: "
                + textoOuPadrao(motivoReprovacao, MOTIVO_PADRAO_REPROVACAO));
    }

    public boolean estaAprovada() {
        return status == StatusSolicitacao.APROVADO;
    }

    private void decidirPelaMesa(StatusSolicitacao novoStatus, String novoParecer) {
        if (!status.aguardaDecisaoDaMesa()) {
            throw new TransicaoStatusInvalidaException(id, status, novoStatus);
        }
        this.status = novoStatus;
        this.parecer = novoParecer;
        this.dataAtualizacao = LocalDateTime.now();
    }

    private static String textoOuPadrao(String texto, String padrao) {
        return texto == null || texto.isBlank() ? padrao : texto.trim();
    }
}
