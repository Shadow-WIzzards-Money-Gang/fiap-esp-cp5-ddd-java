package br.com.fiap.bank.plataforma.domain.exception;

/**
 * Dado que nao satisfaz a invariante de um Value Object ou de uma entidade.
 */
public class ValorInvalidoException extends DominioException {

    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }
}
