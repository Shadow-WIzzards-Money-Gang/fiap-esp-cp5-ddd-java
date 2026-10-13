package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AtualizarSaldoRequest(

        @NotBlank(message = "Operacao e obrigatoria.")
        @Pattern(regexp = "(?i)DEPOSITO|SAQUE", message = "Operacao deve ser DEPOSITO ou SAQUE.")
        String operacao,

        @NotNull(message = "Valor e obrigatorio.")
        @Positive(message = "Valor deve ser maior que zero.")
        BigDecimal valor
) {
}
