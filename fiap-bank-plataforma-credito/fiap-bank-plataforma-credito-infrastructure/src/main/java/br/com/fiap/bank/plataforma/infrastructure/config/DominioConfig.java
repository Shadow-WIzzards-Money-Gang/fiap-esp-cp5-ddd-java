package br.com.fiap.bank.plataforma.infrastructure.config;

import br.com.fiap.bank.plataforma.domain.gateway.BureauCreditoGateway;
import br.com.fiap.bank.plataforma.domain.gateway.CoreBancarioGateway;
import br.com.fiap.bank.plataforma.domain.gateway.CrmGateway;
import br.com.fiap.bank.plataforma.domain.gateway.ScrGateway;
import br.com.fiap.bank.plataforma.domain.service.AvaliadorDeRisco;
import br.com.fiap.bank.plataforma.domain.service.MotorDeCredito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra como beans os servicos de dominio, que sao Java puro e nao conhecem o Spring.
 */
@Configuration
public class DominioConfig {

    @Bean
    public MotorDeCredito motorDeCredito() {
        return new MotorDeCredito();
    }

    @Bean
    public AvaliadorDeRisco avaliadorDeRisco(ScrGateway scrGateway, BureauCreditoGateway bureauCreditoGateway,
                                             CrmGateway crmGateway, CoreBancarioGateway coreBancarioGateway) {
        return new AvaliadorDeRisco(scrGateway, bureauCreditoGateway, crmGateway, coreBancarioGateway);
    }
}
