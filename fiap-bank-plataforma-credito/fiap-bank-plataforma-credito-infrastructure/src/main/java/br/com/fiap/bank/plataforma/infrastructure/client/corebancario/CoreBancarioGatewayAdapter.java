package br.com.fiap.bank.plataforma.infrastructure.client.corebancario;

import br.com.fiap.bank.plataforma.domain.exception.IntegracaoExternaException;
import br.com.fiap.bank.plataforma.domain.gateway.CoreBancarioGateway;
import br.com.fiap.bank.plataforma.domain.valueobject.ContaCliente;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreBancarioGatewayAdapter implements CoreBancarioGateway {

    private final CoreBancarioFeignClient coreBancarioFeignClient;

    @Override
    public Optional<ContaCliente> buscarConta(Cpf cpf) {
        try {
            ContaCoreResponse conta = coreBancarioFeignClient.buscarContaPorCpf(cpf.valor());
            return Optional.of(new ContaCliente(conta.numeroConta(), conta.agencia(), conta.saldo(),
                    conta.statusConta()));
        } catch (FeignException.NotFound e) {
            log.info("[CORE BANCARIO] Cliente com CPF {} nao possui conta no Core Bancario.", cpf);
            return Optional.empty();
        } catch (FeignException e) {
            log.error("[CORE BANCARIO] Falha ao consultar a conta do CPF {} (status {}).", cpf, e.status(), e);
            throw new IntegracaoExternaException("Core Bancario indisponivel ao consultar a conta do CPF "
                    + cpf + ".", e);
        }
    }
}
