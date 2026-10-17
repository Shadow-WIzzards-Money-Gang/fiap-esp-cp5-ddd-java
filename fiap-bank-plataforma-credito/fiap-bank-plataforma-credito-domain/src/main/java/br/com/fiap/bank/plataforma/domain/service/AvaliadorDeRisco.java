package br.com.fiap.bank.plataforma.domain.service;

import br.com.fiap.bank.plataforma.domain.gateway.BureauCreditoGateway;
import br.com.fiap.bank.plataforma.domain.gateway.CoreBancarioGateway;
import br.com.fiap.bank.plataforma.domain.gateway.CrmGateway;
import br.com.fiap.bank.plataforma.domain.gateway.ScrGateway;
import br.com.fiap.bank.plataforma.domain.valueobject.AnaliseRisco;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import lombok.RequiredArgsConstructor;

/**
 * Reune os indicadores de risco do cliente consultando os sistemas externos pelos seus ports.
 */
@RequiredArgsConstructor
public class AvaliadorDeRisco {

    private final ScrGateway scrGateway;
    private final BureauCreditoGateway bureauCreditoGateway;
    private final CrmGateway crmGateway;
    private final CoreBancarioGateway coreBancarioGateway;

    public AnaliseRisco avaliar(Cpf cpf) {
        return new AnaliseRisco(
                scrGateway.possuiRestricao(cpf),
                bureauCreditoGateway.consultarScore(cpf),
                crmGateway.possuiHistoricoNegativo(cpf),
                coreBancarioGateway.buscarConta(cpf));
    }
}
