package br.com.fiap.bank.plataforma.infrastructure.client.notificacao;

import br.com.fiap.bank.plataforma.domain.gateway.NotificacaoGateway;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Simulacao do Sistema de Notificacoes: registra no log o e-mail/SMS que seria enviado ao cliente.
 */
@Slf4j
@Component
public class NotificacaoGatewayLog implements NotificacaoGateway {

    @Override
    public void notificar(Cpf cpf, String mensagem) {
        log.info("[SISTEMA DE NOTIFICACOES] Notificacao enviada para CPF {}: {}", cpf, mensagem);
    }
}
