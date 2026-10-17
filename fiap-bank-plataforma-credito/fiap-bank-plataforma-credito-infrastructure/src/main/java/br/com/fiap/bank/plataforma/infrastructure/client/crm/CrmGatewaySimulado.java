package br.com.fiap.bank.plataforma.infrastructure.client.crm;

import br.com.fiap.bank.plataforma.domain.gateway.CrmGateway;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import org.springframework.stereotype.Component;

/**
 * Simulacao do CRM corporativo: CPFs terminados em 8 possuem inadimplencia anterior com o FIAP Bank.
 */
@Component
public class CrmGatewaySimulado implements CrmGateway {

    @Override
    public boolean possuiHistoricoNegativo(Cpf cpf) {
        return cpf.valor().endsWith("8");
    }
}
