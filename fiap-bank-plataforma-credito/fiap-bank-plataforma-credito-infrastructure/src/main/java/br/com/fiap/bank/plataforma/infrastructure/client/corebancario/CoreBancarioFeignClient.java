package br.com.fiap.bank.plataforma.infrastructure.client.corebancario;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Rotas RESTful do Core Bancario (/api/v1).
 */
@FeignClient(name = "coreBancarioClient", url = "${core.bancario.url:http://localhost:8080/api/v1}")
public interface CoreBancarioFeignClient {

    @GetMapping("/contas/cliente/{cpf}")
    ContaCoreResponse buscarContaPorCpf(@PathVariable("cpf") String cpf);
}
