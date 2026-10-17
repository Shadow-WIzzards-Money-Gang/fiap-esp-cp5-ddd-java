package br.com.fiap.bank.plataforma.domain.valueobject;

import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;

/**
 * CPF sempre normalizado (somente os 11 digitos), aceitando entrada com ou sem mascara.
 */
public record Cpf(String valor) {

    private static final int TAMANHO = 11;

    public Cpf {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("CPF e obrigatorio.");
        }
        valor = valor.replaceAll("\\D", "");
        if (valor.length() != TAMANHO) {
            throw new ValorInvalidoException("CPF deve conter 11 digitos.");
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}
