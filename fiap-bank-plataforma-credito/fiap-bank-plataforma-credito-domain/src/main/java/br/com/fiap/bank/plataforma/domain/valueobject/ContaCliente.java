package br.com.fiap.bank.plataforma.domain.valueobject;

import java.math.BigDecimal;

/**
 * Visao da conta do cliente no Core Bancario, do ponto de vista da Plataforma de Credito.
 */
public record ContaCliente(
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        String statusConta
) {

    private static final String STATUS_ATIVA = "ATIVA";

    public boolean estaAtiva() {
        return STATUS_ATIVA.equalsIgnoreCase(statusConta);
    }
}
