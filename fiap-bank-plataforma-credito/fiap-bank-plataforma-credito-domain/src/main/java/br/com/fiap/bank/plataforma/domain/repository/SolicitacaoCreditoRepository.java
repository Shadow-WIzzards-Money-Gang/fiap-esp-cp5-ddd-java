package br.com.fiap.bank.plataforma.domain.repository;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoCreditoRepository {

    SolicitacaoCredito salvar(SolicitacaoCredito solicitacao);

    Optional<SolicitacaoCredito> buscarPorId(Long id);

    /** Mais recentes primeiro. */
    List<SolicitacaoCredito> listarPorCpf(Cpf cpf);

    /** Mais recentes primeiro. */
    List<SolicitacaoCredito> listarTodas();
}
