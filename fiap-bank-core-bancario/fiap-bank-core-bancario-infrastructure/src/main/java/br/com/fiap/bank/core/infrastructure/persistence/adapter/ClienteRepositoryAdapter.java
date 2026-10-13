package br.com.fiap.bank.core.infrastructure.persistence.adapter;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.domain.valueobject.Cpf;
import br.com.fiap.bank.core.domain.valueobject.Email;
import br.com.fiap.bank.core.infrastructure.persistence.mapper.ClienteMapper;
import br.com.fiap.bank.core.infrastructure.persistence.repository.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository clienteJpaRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public Cliente salvar(Cliente cliente) {
        return clienteMapper.paraDominio(clienteJpaRepository.save(clienteMapper.paraJpaEntity(cliente)));
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return clienteJpaRepository.findById(id).map(clienteMapper::paraDominio);
    }

    @Override
    public Optional<Cliente> buscarPorCpf(Cpf cpf) {
        return clienteJpaRepository.findByCpf(cpf.valor()).map(clienteMapper::paraDominio);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(Email email) {
        return clienteJpaRepository.findByEmail(email.valor()).map(clienteMapper::paraDominio);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteJpaRepository.findAll().stream().map(clienteMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorCpf(Cpf cpf) {
        return clienteJpaRepository.existsByCpf(cpf.valor());
    }

    @Override
    public void remover(Cliente cliente) {
        clienteJpaRepository.deleteById(cliente.getId());
    }
}
