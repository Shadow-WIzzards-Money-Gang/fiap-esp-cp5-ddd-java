package br.com.fiap.bank.plataforma.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CriarSolicitacaoRequest(

        @NotBlank(message = "CPF do cliente e obrigatorio.")
        String cpfCliente,

        @NotBlank(message = "Nome do cliente e obrigatorio.")
        String nomeCliente,

        @NotBlank(message = "Tipo de credito e obrigatorio.")
        String tipoCredito,

        @NotNull(message = "Valor solicitado e obrigatorio.")
        @Positive(message = "Valor solicitado deve ser maior que zero.")
        BigDecimal valorSolicitado,

        @NotNull(message = "Quantidade de parcelas e obrigatoria.")
        @Positive(message = "Quantidade de parcelas deve ser maior que zero.")
        Integer quantidadeParcelas,

        @NotNull(message = "Renda mensal e obrigatoria.")
        @PositiveOrZero(message = "Renda mensal deve ser maior ou igual a zero.")
        BigDecimal rendaMensal
) {
}
