package br.com.fiap.bank.plataforma.domain.gateway;

import br.com.fiap.bank.plataforma.domain.exception.IntegracaoExternaException;
import br.com.fiap.bank.plataforma.domain.valueobject.ContaCliente;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;

import java.util.Optional;

public interface CoreBancarioGateway {

    /**
     * Vazio quando o cliente nao tem conta no Core Bancario.
     *
     * @throws IntegracaoExternaException se o Core Bancario estiver indisponivel ou responder com erro
     */
    Optional<ContaCliente> buscarConta(Cpf cpf);
}
