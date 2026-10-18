package br.com.fiap.bank.plataforma.application.dto.response;

import br.com.fiap.bank.plataforma.domain.valueobject.ContaCliente;

import java.math.BigDecimal;

/**
 * Nomes de campo mantidos iguais aos do sistema legado (statusConta, saldo, agencia, numeroConta).
 */
public record ContaClienteResponse(
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        String statusConta
) {

    public static ContaClienteResponse de(ContaCliente contaCliente) {
        return new ContaClienteResponse(
                contaCliente.numeroConta(),
                contaCliente.agencia(),
                contaCliente.saldo(),
                contaCliente.statusConta());
    }
}
