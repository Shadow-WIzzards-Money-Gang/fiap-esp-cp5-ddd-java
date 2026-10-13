package br.com.fiap.bank.core.domain.exception;

/**
 * Raiz das excecoes de dominio: toda violacao de regra ou invariante do Core Bancario.
 */
public class DominioException extends RuntimeException {

    public DominioException(String mensagem) {
        super(mensagem);
    }
}
