package br.com.fiap.bank.core.presentation.exception;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Corpo padronizado de erro. A lista campos so vem preenchida em erros de validacao.
 */
public record ErroResponse(
        OffsetDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        List<CampoErro> campos
) {
}
