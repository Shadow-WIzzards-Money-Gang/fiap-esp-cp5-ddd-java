package br.com.fiap.bank.plataforma.infrastructure.client.corebancario;

import java.math.BigDecimal;

/**
 * Subconjunto do ContaResponse do Core Bancario usado pela Plataforma; campos extras sao ignorados.
 */
public record ContaCoreResponse(
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        String statusConta
) {
}
