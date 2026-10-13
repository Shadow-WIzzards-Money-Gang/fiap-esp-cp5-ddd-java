package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.exception.RegraNegocioException;
import br.com.fiap.bank.core.domain.model.ContaBancaria;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * Liga o nome da operacao recebido na requisicao ao metodo de negocio da conta.
 */
enum OperacaoSaldo {

    DEPOSITO(ContaBancaria::depositar),
    SAQUE(ContaBancaria::sacar);

    private final BiConsumer<ContaBancaria, BigDecimal> metodoDeNegocio;

    OperacaoSaldo(BiConsumer<ContaBancaria, BigDecimal> metodoDeNegocio) {
        this.metodoDeNegocio = metodoDeNegocio;
    }

    static OperacaoSaldo de(String nome) {
        try {
            return valueOf(nome.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RegraNegocioException("Operacao deve ser DEPOSITO ou SAQUE.");
        }
    }

    void aplicar(ContaBancaria conta, BigDecimal valor) {
        metodoDeNegocio.accept(conta, valor);
    }
}
