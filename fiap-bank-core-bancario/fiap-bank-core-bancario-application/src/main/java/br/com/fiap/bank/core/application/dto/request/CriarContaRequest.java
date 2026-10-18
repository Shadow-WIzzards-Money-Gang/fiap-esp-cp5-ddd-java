package br.com.fiap.bank.core.application.dto.request;

import br.com.fiap.bank.core.domain.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CriarContaRequest(

        @NotBlank(message = "CPF do cliente e obrigatorio.")
        String cpfCliente,

        /** Opcional: quando ausente, a conta e aberta na agencia padrao. */
        @Pattern(regexp = "\\d{4}", message = "Agencia deve conter 4 digitos.")
        String agencia,

        @NotNull(message = "Tipo da conta e obrigatorio.")
        TipoConta tipoConta,

        @NotNull(message = "Saldo inicial e obrigatorio.")
        @PositiveOrZero(message = "Saldo inicial nao pode ser negativo.")
        BigDecimal saldoInicial
) {
}
