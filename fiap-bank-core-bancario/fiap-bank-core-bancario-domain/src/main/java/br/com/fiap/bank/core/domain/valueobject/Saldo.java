package br.com.fiap.bank.core.domain.valueobject;

import br.com.fiap.bank.core.domain.exception.SaldoInsuficienteException;
import br.com.fiap.bank.core.domain.exception.ValorInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Saldo monetario com 2 casas decimais. Nunca negativo; toda operacao devolve um novo Saldo.
 */
public record Saldo(BigDecimal valor) {

    private static final int CASAS_DECIMAIS = 2;

    public Saldo {
        if (valor == null) {
            throw new ValorInvalidoException("Saldo e obrigatorio.");
        }
        if (valor.signum() < 0) {
            throw new ValorInvalidoException("Saldo nao pode ser negativo.");
        }
        valor = valor.setScale(CASAS_DECIMAIS, RoundingMode.HALF_UP);
    }

    public Saldo adicionar(BigDecimal quantia) {
        validarQuantia(quantia);
        return new Saldo(valor.add(quantia));
    }

    public Saldo subtrair(BigDecimal quantia) {
        validarQuantia(quantia);
        if (valor.compareTo(quantia) < 0) {
            throw new SaldoInsuficienteException(valor, quantia);
        }
        return new Saldo(valor.subtract(quantia));
    }

    private static void validarQuantia(BigDecimal quantia) {
        if (quantia == null || quantia.signum() <= 0) {
            throw new ValorInvalidoException("O valor da operacao deve ser maior que zero.");
        }
    }

    @Override
    public String toString() {
        return valor.toPlainString();
    }
}
