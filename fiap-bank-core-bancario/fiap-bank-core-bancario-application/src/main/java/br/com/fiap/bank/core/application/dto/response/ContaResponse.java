package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.enums.StatusConta;
import br.com.fiap.bank.core.domain.enums.TipoConta;
import br.com.fiap.bank.core.domain.model.ContaBancaria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Os campos statusConta, saldo, agencia e numeroConta sao consumidos pela Plataforma de Credito
 * e pelo Internet Banking; nao renomear.
 */
public record ContaResponse(
        UUID id,
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        StatusConta statusConta,
        TipoConta tipoConta,
        LocalDate dataAbertura
) {

    public static ContaResponse de(ContaBancaria conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNumero(),
                conta.getAgencia(),
                conta.getSaldo().valor(),
                conta.getStatus(),
                conta.getTipo(),
                conta.getDataAbertura());
    }
}
