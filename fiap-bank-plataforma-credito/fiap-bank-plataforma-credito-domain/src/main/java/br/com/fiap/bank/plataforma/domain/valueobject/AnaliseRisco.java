package br.com.fiap.bank.plataforma.domain.valueobject;

import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;

import java.util.Optional;

/**
 * Indicadores de risco do cliente coletados no SCR, Bureau de Credito, CRM e Core Bancario.
 *
 * @param conta vazia quando o cliente nao tem conta no Core ou o Core nao respondeu
 */
public record AnaliseRisco(
        boolean restricaoScr,
        int scoreBureau,
        boolean historicoNegativoCrm,
        Optional<ContaCliente> conta
) {

    private static final int SCORE_MINIMO = 0;
    private static final int SCORE_MAXIMO = 1000;

    public AnaliseRisco {
        if (scoreBureau < SCORE_MINIMO || scoreBureau > SCORE_MAXIMO) {
            throw new ValorInvalidoException("Score do Bureau deve estar entre " + SCORE_MINIMO + " e "
                    + SCORE_MAXIMO + ".");
        }
        conta = conta == null ? Optional.empty() : conta;
    }

    public boolean possuiContaAtiva() {
        return conta.map(ContaCliente::estaAtiva).orElse(false);
    }
}
