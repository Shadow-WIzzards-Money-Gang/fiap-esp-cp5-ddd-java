package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.application.exception.RegraNegocioException;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;

import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * Liga o status pedido pela Mesa de Operacoes ao metodo de negocio da solicitacao.
 */
enum DecisaoMesa {

    APROVADO(SolicitacaoCredito::aprovar),
    REPROVADO(SolicitacaoCredito::reprovar);

    private final BiConsumer<SolicitacaoCredito, String> metodoDeNegocio;

    DecisaoMesa(BiConsumer<SolicitacaoCredito, String> metodoDeNegocio) {
        this.metodoDeNegocio = metodoDeNegocio;
    }

    static DecisaoMesa de(String status) {
        try {
            return valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RegraNegocioException("Status deve ser APROVADO ou REPROVADO.");
        }
    }

    void aplicar(SolicitacaoCredito solicitacao, String parecer) {
        metodoDeNegocio.accept(solicitacao, parecer);
    }
}
