package br.com.fiap.bank.plataforma.domain.enums;

public enum StatusSolicitacao {
    EM_ANALISE,
    APROVADO,
    REPROVADO,
    DOCUMENTACAO_PENDENTE;

    /**
     * Solicitacoes que ainda dependem de uma decisao do operador da Mesa de Operacoes.
     */
    public boolean aguardaDecisaoDaMesa() {
        return this == EM_ANALISE || this == DOCUMENTACAO_PENDENTE;
    }
}
