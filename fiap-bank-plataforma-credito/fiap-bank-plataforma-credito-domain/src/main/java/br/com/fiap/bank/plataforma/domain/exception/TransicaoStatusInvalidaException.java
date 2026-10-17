package br.com.fiap.bank.plataforma.domain.exception;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;

public class TransicaoStatusInvalidaException extends DominioException {

    public TransicaoStatusInvalidaException(Long solicitacaoId, StatusSolicitacao statusAtual,
                                            StatusSolicitacao statusDesejado) {
        super("Solicitacao " + solicitacaoId + " nao pode passar de " + statusAtual + " para "
                + statusDesejado + ": apenas solicitacoes " + StatusSolicitacao.EM_ANALISE
                + " podem ser decididas pela Mesa de Operacoes.");
    }
}
