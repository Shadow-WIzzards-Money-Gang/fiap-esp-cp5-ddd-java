package br.com.fiap.bank.plataforma.domain.gateway;

import br.com.fiap.bank.plataforma.domain.exception.IntegracaoExternaException;
import br.com.fiap.bank.plataforma.domain.valueobject.ContaCliente;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;

import java.util.Optional;

public interface CoreBancarioGateway {

    /**
     * Vazio quando o cliente nao tem conta ou o Core Bancario esta indisponivel.
     */
    Optional<ContaCliente> buscarConta(Cpf cpf);

    /**
     * Desembolso do credito aprovado na conta do cliente.
     *
     * @throws IntegracaoExternaException se o Core Bancario nao confirmar o credito
     */
    void creditar(String numeroConta, Dinheiro valor);
}
