package br.com.fiap.bank.plataforma.domain.gateway;

import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;

/**
 * CRM corporativo do FIAP Bank (historico de relacionamento do cliente).
 */
public interface CrmGateway {

    /** Indica inadimplencia anterior com o proprio FIAP Bank. */
    boolean possuiHistoricoNegativo(Cpf cpf);
}
