package br.com.fiap.bank.plataforma.domain.valueobject;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;

/**
 * Resultado do motor de credito: o status inicial da solicitacao e a justificativa.
 */
public record DecisaoCredito(StatusSolicitacao status, String parecer) {

    public DecisaoCredito {
        if (status == null) {
            throw new ValorInvalidoException("Status da decisao e obrigatorio.");
        }
        if (parecer == null || parecer.isBlank()) {
            throw new ValorInvalidoException("Parecer da decisao e obrigatorio.");
        }
    }
}
