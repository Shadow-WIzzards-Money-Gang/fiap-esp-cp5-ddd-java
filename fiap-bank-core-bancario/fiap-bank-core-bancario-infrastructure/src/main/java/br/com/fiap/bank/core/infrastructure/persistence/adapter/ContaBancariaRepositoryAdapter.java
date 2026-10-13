package br.com.fiap.bank.core.infrastructure.persistence.adapter;

import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.repository.ContaBancariaRepository;
import br.com.fiap.bank.core.domain.valueobject.Cpf;
import br.com.fiap.bank.core.infrastructure.persistence.mapper.ContaBancariaMapper;
import br.com.fiap.bank.core.infrastructure.persistence.repository.ContaBancariaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContaBancariaRepositoryAdapter implements ContaBancariaRepository {

    private final ContaBancariaJpaRepository contaBancariaJpaRepository;
    private final ContaBancariaMapper contaBancariaMapper;

    @Override
    public ContaBancaria salvar(ContaBancaria conta) {
        return contaBancariaMapper.paraDominio(
                contaBancariaJpaRepository.save(contaBancariaMapper.paraJpaEntity(conta)));
    }

    @Override
    public Optional<ContaBancaria> buscarPorNumero(String numero) {
        return contaBancariaJpaRepository.findByNumero(numero).map(contaBancariaMapper::paraDominio);
    }

    @Override
    public Optional<ContaBancaria> buscarPorCpfDoCliente(Cpf cpf) {
        return contaBancariaJpaRepository.findByCpfDoCliente(cpf.valor()).map(contaBancariaMapper::paraDominio);
    }

    @Override
    public List<ContaBancaria> listarTodas() {
        return contaBancariaJpaRepository.findAll().stream().map(contaBancariaMapper::paraDominio).toList();
    }

    @Override
    public boolean existePorNumero(String numero) {
        return contaBancariaJpaRepository.existsByNumero(numero);
    }
}
