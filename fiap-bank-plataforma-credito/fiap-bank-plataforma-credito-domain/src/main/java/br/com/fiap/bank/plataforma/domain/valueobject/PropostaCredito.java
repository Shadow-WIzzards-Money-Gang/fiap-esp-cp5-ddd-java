package br.com.fiap.bank.plataforma.domain.valueobject;

import br.com.fiap.bank.plataforma.domain.enums.TipoCredito;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;

/**
 * Dados da proposta enviados pelo cliente. Imutavel: nao muda depois que a solicitacao e registrada.
 */
public record PropostaCredito(
        Cpf cpf,
        String nomeCliente,
        TipoCredito tipoCredito,
        Dinheiro valorSolicitado,
        int quantidadeParcelas,
        Dinheiro rendaMensal
) {

    public PropostaCredito {
        if (cpf == null) {
            throw new ValorInvalidoException("CPF do cliente e obrigatorio.");
        }
        if (nomeCliente == null || nomeCliente.isBlank()) {
            throw new ValorInvalidoException("Nome do cliente e obrigatorio.");
        }
        if (tipoCredito == null) {
            throw new ValorInvalidoException("Tipo de credito e obrigatorio.");
        }
        if (valorSolicitado == null || !valorSolicitado.ehPositivo()) {
            throw new ValorInvalidoException("Valor solicitado deve ser maior que zero.");
        }
        if (quantidadeParcelas <= 0) {
            throw new ValorInvalidoException("Quantidade de parcelas deve ser maior que zero.");
        }
        if (rendaMensal == null) {
            throw new ValorInvalidoException("Renda mensal e obrigatoria.");
        }
        nomeCliente = nomeCliente.trim();
    }

    /**
     * Parcela sem juros, usada pelo motor de credito para medir o comprometimento da renda.
     */
    public Dinheiro parcelaEstimada() {
        return valorSolicitado.dividir(quantidadeParcelas);
    }
}
