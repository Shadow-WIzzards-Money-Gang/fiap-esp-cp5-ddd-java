package br.com.fiap.bank.core.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados cadastrais que podem ser alterados. O CPF identifica o cliente e nao muda.
 */
public record AtualizarClienteRequest(

        @NotBlank(message = "Nome e obrigatorio.")
        @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres.")
        String nome,

        @NotBlank(message = "E-mail e obrigatorio.")
        @Email(message = "E-mail invalido.")
        String email,

        @NotNull(message = "Data de nascimento e obrigatoria.")
        @Past(message = "Data de nascimento deve estar no passado.")
        LocalDate dataNascimento,

        @NotNull(message = "Renda mensal e obrigatoria.")
        @PositiveOrZero(message = "Renda mensal nao pode ser negativa.")
        BigDecimal rendaMensal
) {
}
