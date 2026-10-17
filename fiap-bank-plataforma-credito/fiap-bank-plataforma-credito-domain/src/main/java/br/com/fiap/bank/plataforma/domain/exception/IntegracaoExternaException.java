package br.com.fiap.bank.plataforma.domain.exception;

/**
 * Falha ao se comunicar com um sistema externo (Core Bancario, SCR, Bureau, CRM, Notificacoes).
 * Nao e violacao de regra de negocio, por isso nao estende {@link DominioException}.
 */
public class IntegracaoExternaException extends RuntimeException {

    public IntegracaoExternaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
