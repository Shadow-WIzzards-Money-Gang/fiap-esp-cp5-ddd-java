package br.com.fiap.bank.core.application.exception;

/**
 * Operacao valida que nao pode ser concluida no estado atual do recurso (ex.: saldo insuficiente).
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
