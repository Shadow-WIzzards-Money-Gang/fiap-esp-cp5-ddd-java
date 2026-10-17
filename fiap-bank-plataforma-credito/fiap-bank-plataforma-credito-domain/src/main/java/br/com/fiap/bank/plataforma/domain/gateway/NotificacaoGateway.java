package br.com.fiap.bank.plataforma.domain.gateway;

import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;

/**
 * Sistema de Notificacoes externo (e-mail/SMS ao cliente).
 */
public interface NotificacaoGateway {

    void notificar(Cpf cpf, String mensagem);
}
