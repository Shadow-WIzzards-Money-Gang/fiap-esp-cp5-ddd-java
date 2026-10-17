package br.com.fiap.bank.plataforma.domain.gateway;

import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;

/**
 * Sistema de Informacoes de Credito do Banco Central (endividamento em outras instituicoes).
 */
public interface ScrGateway {

    boolean possuiRestricao(Cpf cpf);
}
