package br.com.fiap.bank.plataforma.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarStatusRequest(

        @NotBlank(message = "Status e obrigatorio.")
        @Pattern(regexp = "(?i)APROVADO|REPROVADO", message = "Status deve ser APROVADO ou REPROVADO.")
        String status,

        String parecer
) {
}
