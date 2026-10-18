package br.com.fiap.bank.plataforma.application.exception;

/**
 * Falha ao se comunicar com um sistema externo (Core Bancario, SCR, Bureau, Notificacoes).
 */
public class IntegracaoIndisponivelException extends RuntimeException {

    public IntegracaoIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
