package br.com.fiap.bank.core.domain.valueobject;

import br.com.fiap.bank.core.domain.exception.ValorInvalidoException;

import java.util.regex.Pattern;

/**
 * E-mail normalizado (sem espacos nas pontas e em minusculas).
 */
public record Email(String valor) {

    private static final Pattern FORMATO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("E-mail e obrigatorio.");
        }
        valor = valor.trim().toLowerCase();
        if (!FORMATO.matcher(valor).matches()) {
            throw new ValorInvalidoException("E-mail invalido: " + valor + ".");
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}
