package br.com.fiap.bank.core.domain.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends DominioException {

    public SaldoInsuficienteException(BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super("Saldo insuficiente: saldo atual " + saldoAtual.toPlainString()
                + ", valor solicitado " + valorSolicitado.toPlainString() + ".");
    }
}
