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

    /** Fallback das rotas legadas: o cliente nao tem conta no Core Bancario. */
    public static final String STATUS_SEM_CONTA = "SEM_CONTA";
    /** Fallback das rotas legadas: o Core Bancario nao respondeu. */
    public static final String STATUS_INDISPONIVEL = "INDISPONIVEL";

    public static ContaClienteResponse semConta() {
        return new ContaClienteResponse(null, null, null, STATUS_SEM_CONTA);
    }

    public static ContaClienteResponse indisponivel() {
        return new ContaClienteResponse(null, null, null, STATUS_INDISPONIVEL);
    }

    public static ContaClienteResponse de(ContaCliente contaCliente) {
        return new ContaClienteResponse(
                contaCliente.numeroConta(),
                contaCliente.agencia(),
                contaCliente.saldo(),
                contaCliente.statusConta());
    }
}
