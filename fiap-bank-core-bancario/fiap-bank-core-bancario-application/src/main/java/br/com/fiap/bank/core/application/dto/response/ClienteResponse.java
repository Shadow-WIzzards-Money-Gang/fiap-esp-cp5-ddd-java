package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.model.Cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nome,
        String email,
        String cpf,
        LocalDate dataNascimento,
        BigDecimal rendaMensal,
        /** Nulo enquanto o cliente ainda nao abriu conta. */
        ContaResponse conta
) {

    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail().valor(),
                cliente.getCpf().valor(),
                cliente.getDataNascimento(),
                cliente.getRendaMensal(),
                cliente.possuiConta() ? ContaResponse.de(cliente.getConta()) : null);
    }
}
