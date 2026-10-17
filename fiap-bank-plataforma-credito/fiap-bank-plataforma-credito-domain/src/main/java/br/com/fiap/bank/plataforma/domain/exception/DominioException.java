package br.com.fiap.bank.plataforma.domain.exception;

/**
 * Raiz das excecoes de dominio: toda violacao de regra ou invariante da Plataforma de Credito.
 */
public class DominioException extends RuntimeException {

    public DominioException(String mensagem) {
        super(mensagem);
    }
}
