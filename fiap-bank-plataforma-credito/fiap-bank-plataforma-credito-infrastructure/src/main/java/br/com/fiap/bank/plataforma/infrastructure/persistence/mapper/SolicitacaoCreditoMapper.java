package br.com.fiap.bank.plataforma.infrastructure.persistence.mapper;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.Cpf;
import br.com.fiap.bank.plataforma.domain.valueobject.Dinheiro;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;
import br.com.fiap.bank.plataforma.infrastructure.persistence.entity.SolicitacaoCreditoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SolicitacaoCreditoMapper {

    public SolicitacaoCredito paraDominio(SolicitacaoCreditoJpaEntity entity) {
        PropostaCredito proposta = new PropostaCredito(
                new Cpf(entity.getCpfCliente()),
                entity.getNomeCliente(),
                entity.getTipoCredito(),
                new Dinheiro(entity.getValorSolicitado()),
                entity.getQuantidadeParcelas(),
                new Dinheiro(entity.getRendaMensal()));

        return SolicitacaoCredito.reconstituir(
                entity.getId(),
                proposta,
                entity.getStatus(),
                entity.getParecer(),
                entity.getRestricaoScr(),
                entity.getScoreBureau(),
                entity.getDataSolicitacao(),
                entity.getDataAtualizacao());
    }

    public SolicitacaoCreditoJpaEntity paraJpaEntity(SolicitacaoCredito solicitacao) {
        PropostaCredito proposta = solicitacao.getProposta();
        return SolicitacaoCreditoJpaEntity.builder()
                .id(solicitacao.getId())
                .cpfCliente(proposta.cpf().valor())
                .nomeCliente(proposta.nomeCliente())
                .tipoCredito(proposta.tipoCreditoInformado())
                .valorSolicitado(proposta.valorSolicitado().valor())
                .quantidadeParcelas(proposta.quantidadeParcelas())
                .rendaMensal(proposta.rendaMensal().valor())
                .status(solicitacao.getStatus())
                .parecer(solicitacao.getParecer())
                .restricaoScr(solicitacao.isRestricaoScr())
                .scoreBureau(solicitacao.getScoreBureau())
                .dataSolicitacao(solicitacao.getDataSolicitacao())
                .dataAtualizacao(solicitacao.getDataAtualizacao())
                .build();
    }
}
