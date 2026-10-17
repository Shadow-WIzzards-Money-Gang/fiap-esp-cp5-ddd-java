package br.com.fiap.bank.plataforma.infrastructure.client.corebancario;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Rotas RESTful do Core Bancario (/api/v1).
 */
@FeignClient(name = "coreBancarioClient", url = "${core.bancario.url:http://localhost:8080/api/v1}")
public interface CoreBancarioFeignClient {

    @GetMapping("/contas/cliente/{cpf}")
    ContaCoreResponse buscarContaPorCpf(@PathVariable("cpf") String cpf);

    @PutMapping("/contas/{numeroConta}/saldo")
    ContaCoreResponse atualizarSaldo(@PathVariable("numeroConta") String numeroConta,
                                     @RequestBody AtualizarSaldoCoreRequest request);
}
