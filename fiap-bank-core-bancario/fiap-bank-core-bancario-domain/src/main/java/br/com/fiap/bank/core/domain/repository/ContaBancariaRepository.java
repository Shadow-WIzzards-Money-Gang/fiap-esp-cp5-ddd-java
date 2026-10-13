package br.com.fiap.bank.core.domain.repository;

import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.valueobject.Cpf;

import java.util.List;
import java.util.Optional;

public interface ContaBancariaRepository {

    ContaBancaria salvar(ContaBancaria conta);

    Optional<ContaBancaria> buscarPorNumero(String numero);

    Optional<ContaBancaria> buscarPorCpfDoCliente(Cpf cpf);

    List<ContaBancaria> listarTodas();

    boolean existePorNumero(String numero);
}
