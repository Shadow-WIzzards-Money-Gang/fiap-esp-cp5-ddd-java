package br.com.fiap.bank.plataforma.infrastructure.client.bureau;

import br.com.fiap.bank.plataforma.domain.gateway.BureauCreditoGateway;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import org.springframework.stereotype.Component;

/**
 * Simulacao do Bureau de Credito (Serasa/SPC): score deterministico entre 400 e 950 calculado pelo CPF.
 */
@Component
public class BureauCreditoGatewaySimulado implements BureauCreditoGateway {

    private static final int SCORE_BASE = 400;
    private static final int FAIXA_SCORE = 551;

    @Override
    public int consultarScore(Cpf cpf) {
        int somaDigitos = cpf.valor().chars().map(Character::getNumericValue).sum();
        return SCORE_BASE + (somaDigitos * 9) % FAIXA_SCORE;
    }
}
