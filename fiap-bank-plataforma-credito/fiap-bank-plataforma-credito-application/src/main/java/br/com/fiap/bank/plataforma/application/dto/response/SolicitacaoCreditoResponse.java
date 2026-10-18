package br.com.fiap.bank.plataforma.application.dto.response;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.valueobject.PropostaCredito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Nomes de campo mantidos iguais aos do sistema legado: consumidos pela Mesa e pelo Internet Banking.
 */
public record SolicitacaoCreditoResponse(
        Long id,
        String cpfCliente,
        String nomeCliente,
        String tipoCredito,
        BigDecimal valorSolicitado,
        Integer quantidadeParcelas,
        BigDecimal rendaMensal,
        Integer scoreCredito,
        StatusSolicitacao status,
        String parecerOperador,
        Boolean scrRestricoes,
        Integer scoreBureau,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataAtualizacao
) {

    public static SolicitacaoCreditoResponse de(SolicitacaoCredito solicitacao) {
        PropostaCredito proposta = solicitacao.getProposta();
        return new SolicitacaoCreditoResponse(
                solicitacao.getId(),
                proposta.cpf().valor(),
                proposta.nomeCliente(),
                proposta.tipoCreditoInformado(),
                proposta.valorSolicitado().valor(),
                proposta.quantidadeParcelas(),
                proposta.rendaMensal().valor(),
                solicitacao.getScoreCredito(),
                solicitacao.getStatus(),
                solicitacao.getParecer(),
                solicitacao.isRestricaoScr(),
                solicitacao.getScoreBureau(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getDataAtualizacao());
    }
}
