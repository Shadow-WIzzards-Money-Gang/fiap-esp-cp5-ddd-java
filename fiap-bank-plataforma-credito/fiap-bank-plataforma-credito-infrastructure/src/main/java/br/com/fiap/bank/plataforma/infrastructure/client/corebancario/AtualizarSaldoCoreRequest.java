package br.com.fiap.bank.plataforma.infrastructure.client.corebancario;

import java.math.BigDecimal;

/**
 * Corpo do PUT /contas/{numeroConta}/saldo do Core Bancario.
 */
public record AtualizarSaldoCoreRequest(String operacao, BigDecimal valor) {

    private static final String DEPOSITO = "DEPOSITO";

    public static AtualizarSaldoCoreRequest deposito(BigDecimal valor) {
        return new AtualizarSaldoCoreRequest(DEPOSITO, valor);
    }
}
