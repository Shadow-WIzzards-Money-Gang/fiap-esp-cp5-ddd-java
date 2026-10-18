package br.com.fiap.bank.plataforma.presentation.legacy.dto;

import java.math.BigDecimal;

public record SolicitarCreditoLegacyRequest(
        String cpfCliente,
        String nomeCliente,
        String tipoCredito,
        BigDecimal valorSolicitado,
        Integer quantidadeParcelas,
        BigDecimal rendaMensal
) {
}
