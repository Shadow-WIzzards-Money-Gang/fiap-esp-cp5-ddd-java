package br.com.fiap.bank.core.infrastructure.persistence.mapper;

import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.valueobject.Saldo;
import br.com.fiap.bank.core.infrastructure.persistence.entity.ContaBancariaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ContaBancariaMapper {

    public ContaBancaria paraDominio(ContaBancariaJpaEntity entity) {
        return ContaBancaria.reconstituir(
                entity.getId(),
                entity.getNumero(),
                entity.getAgencia(),
                new Saldo(entity.getSaldo()),
                entity.getDataAbertura(),
                entity.getStatus(),
                entity.getTipo());
    }

    public ContaBancariaJpaEntity paraJpaEntity(ContaBancaria conta) {
        return ContaBancariaJpaEntity.builder()
                .id(conta.getId())
                .numero(conta.getNumero())
                .agencia(conta.getAgencia())
                .saldo(conta.getSaldo().valor())
                .dataAbertura(conta.getDataAbertura())
                .status(conta.getStatus())
                .tipo(conta.getTipo())
                .build();
    }
}
