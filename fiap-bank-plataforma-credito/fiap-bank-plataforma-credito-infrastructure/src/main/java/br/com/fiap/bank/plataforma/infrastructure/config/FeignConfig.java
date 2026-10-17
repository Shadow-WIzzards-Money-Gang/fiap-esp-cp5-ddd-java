package br.com.fiap.bank.plataforma.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Fica no infrastructure porque o presentation nao enxerga o OpenFeign em tempo de compilacao.
 */
@Configuration
@EnableFeignClients(basePackages = "br.com.fiap.bank.plataforma.infrastructure.client")
public class FeignConfig {
}
