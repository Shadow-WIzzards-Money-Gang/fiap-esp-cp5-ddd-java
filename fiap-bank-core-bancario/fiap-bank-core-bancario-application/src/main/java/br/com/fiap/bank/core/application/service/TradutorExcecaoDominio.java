package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.exception.ConflitoException;
import br.com.fiap.bank.core.application.exception.RegraNegocioException;
import br.com.fiap.bank.core.domain.exception.DominioException;
import br.com.fiap.bank.core.domain.exception.SaldoInsuficienteException;

import java.util.function.Supplier;

/**
 * Converte excecoes de dominio em excecoes de aplicacao. O presentation nao enxerga o domain
 * em tempo de compilacao, entao so consegue tratar o que sai daqui.
 */
final class TradutorExcecaoDominio {

    private TradutorExcecaoDominio() {
    }

    static <T> T obter(Supplier<T> acao) {
        try {
            return acao.get();
        } catch (SaldoInsuficienteException e) {
            throw new ConflitoException(e.getMessage());
        } catch (DominioException e) {
            throw new RegraNegocioException(e.getMessage());
        }
    }

    static void executar(Runnable acao) {
        obter(() -> {
            acao.run();
            return null;
        });
    }
}
