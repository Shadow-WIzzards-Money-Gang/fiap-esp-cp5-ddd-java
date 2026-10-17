package br.com.fiap.bank.plataforma.infrastructure.persistence.adapter;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.infrastructure.persistence.mapper.SolicitacaoCreditoMapper;
import br.com.fiap.bank.plataforma.infrastructure.persistence.repository.SolicitacaoCreditoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SolicitacaoCreditoRepositoryAdapter implements SolicitacaoCreditoRepository {

    private final SolicitacaoCreditoJpaRepository solicitacaoCreditoJpaRepository;
    private final SolicitacaoCreditoMapper solicitacaoCreditoMapper;

    @Override
    public SolicitacaoCredito salvar(SolicitacaoCredito solicitacao) {
        return solicitacaoCreditoMapper.paraDominio(
                solicitacaoCreditoJpaRepository.save(solicitacaoCreditoMapper.paraJpaEntity(solicitacao)));
    }

    @Override
    public Optional<SolicitacaoCredito> buscarPorId(Long id) {
        return solicitacaoCreditoJpaRepository.findById(id).map(solicitacaoCreditoMapper::paraDominio);
    }

    @Override
    public List<SolicitacaoCredito> listarPorCpf(Cpf cpf) {
        return solicitacaoCreditoJpaRepository.findByCpfClienteOrderByDataSolicitacaoDesc(cpf.valor()).stream()
                .map(solicitacaoCreditoMapper::paraDominio)
                .toList();
    }

    @Override
    public List<SolicitacaoCredito> listarTodas() {
        return solicitacaoCreditoJpaRepository.findAllByOrderByDataSolicitacaoDesc().stream()
                .map(solicitacaoCreditoMapper::paraDominio)
                .toList();
    }
}
