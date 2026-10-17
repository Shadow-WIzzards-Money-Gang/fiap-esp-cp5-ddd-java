package br.com.fiap.bank.plataforma.domain.valueobject;

import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Valor monetario com 2 casas decimais. Nunca negativo; toda operacao devolve um novo Dinheiro.
 */
public record Dinheiro(BigDecimal valor) {

    private static final int CASAS_DECIMAIS = 2;

    public Dinheiro {
        if (valor == null) {
            throw new ValorInvalidoException("Valor monetario e obrigatorio.");
        }
        if (valor.signum() < 0) {
            throw new ValorInvalidoException("Valor monetario nao pode ser negativo.");
        }
        valor = valor.setScale(CASAS_DECIMAIS, RoundingMode.HALF_UP);
    }

    public static Dinheiro de(String valor) {
        return new Dinheiro(new BigDecimal(valor));
    }

    public boolean ehPositivo() {
        return valor.signum() > 0;
    }

    public boolean maiorQue(Dinheiro outro) {
        return valor.compareTo(outro.valor) > 0;
    }

    public Dinheiro multiplicar(BigDecimal fator) {
        return new Dinheiro(valor.multiply(fator));
    }

    public Dinheiro dividir(int divisor) {
        if (divisor <= 0) {
            throw new ValorInvalidoException("Divisor deve ser maior que zero.");
        }
        return new Dinheiro(valor.divide(BigDecimal.valueOf(divisor), CASAS_DECIMAIS, RoundingMode.HALF_UP));
    }

    @Override
    public String toString() {
        return valor.toPlainString();
    }
}
