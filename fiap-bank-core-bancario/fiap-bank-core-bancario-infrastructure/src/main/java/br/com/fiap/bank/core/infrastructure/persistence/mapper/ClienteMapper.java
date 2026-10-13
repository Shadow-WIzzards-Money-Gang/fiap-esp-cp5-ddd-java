package br.com.fiap.bank.core.infrastructure.persistence.mapper;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.valueobject.Cpf;
import br.com.fiap.bank.core.domain.valueobject.Email;
import br.com.fiap.bank.core.infrastructure.persistence.entity.ClienteJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMapper {

    private final ContaBancariaMapper contaBancariaMapper;

    public Cliente paraDominio(ClienteJpaEntity entity) {
        return Cliente.reconstituir(
                entity.getId(),
                entity.getNome(),
                new Email(entity.getEmail()),
                entity.getDataNascimento(),
                new Cpf(entity.getCpf()),
                entity.getRendaMensal(),
                entity.getConta() == null ? null : contaBancariaMapper.paraDominio(entity.getConta()));
    }

    public ClienteJpaEntity paraJpaEntity(Cliente cliente) {
        return ClienteJpaEntity.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .email(cliente.getEmail().valor())
                .dataNascimento(cliente.getDataNascimento())
                .cpf(cliente.getCpf().valor())
                .rendaMensal(cliente.getRendaMensal())
                .conta(cliente.possuiConta() ? contaBancariaMapper.paraJpaEntity(cliente.getConta()) : null)
                .build();
    }
}
