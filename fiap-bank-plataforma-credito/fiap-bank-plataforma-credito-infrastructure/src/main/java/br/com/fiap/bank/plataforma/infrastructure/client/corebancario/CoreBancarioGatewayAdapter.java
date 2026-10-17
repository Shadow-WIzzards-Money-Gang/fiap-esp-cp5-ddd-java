package br.com.fiap.bank.plataforma.infrastructure.client.corebancario;

import br.com.fiap.bank.plataforma.domain.exception.IntegracaoExternaException;
import br.com.fiap.bank.plataforma.domain.gateway.CoreBancarioGateway;
import br.com.fiap.bank.plataforma.domain.valueobject.ContaCliente;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
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
            // Sem dados falsos: a ausencia da conta faz o motor encaminhar a proposta para a Mesa
            log.warn("[CORE BANCARIO] Falha ao consultar a conta do CPF {} (status {}): {}",
                    cpf, e.status(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void creditar(String numeroConta, Dinheiro valor) {
        try {
            coreBancarioFeignClient.atualizarSaldo(numeroConta, AtualizarSaldoCoreRequest.deposito(valor.valor()));
            log.info("[CORE BANCARIO] Desembolso de R$ {} creditado na conta {}.", valor, numeroConta);
        } catch (FeignException e) {
            throw new IntegracaoExternaException("Core Bancario nao confirmou o credito de R$ " + valor
                    + " na conta " + numeroConta + ".", e);
        }
    }
}
