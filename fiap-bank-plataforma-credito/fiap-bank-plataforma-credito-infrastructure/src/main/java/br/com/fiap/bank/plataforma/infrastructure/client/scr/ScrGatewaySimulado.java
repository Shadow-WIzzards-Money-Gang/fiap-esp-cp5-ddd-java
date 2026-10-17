package br.com.fiap.bank.plataforma.infrastructure.client.scr;

import br.com.fiap.bank.plataforma.domain.gateway.ScrGateway;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import org.springframework.stereotype.Component;

/**
 * Simulacao do SCR do Banco Central: CPFs terminados em 9 possuem restricao.
 */
@Component
public class ScrGatewaySimulado implements ScrGateway {

    @Override
    public boolean possuiRestricao(Cpf cpf) {
        return cpf.valor().endsWith("9");
    }
}
