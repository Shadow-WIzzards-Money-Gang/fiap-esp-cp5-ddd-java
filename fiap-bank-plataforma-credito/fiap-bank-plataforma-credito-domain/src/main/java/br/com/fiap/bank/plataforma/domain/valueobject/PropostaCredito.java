package br.com.fiap.bank.plataforma.domain.valueobject;

import br.com.fiap.bank.plataforma.domain.enums.TipoCredito;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;

/**
 * Dados da proposta enviados pelo cliente. Imutavel: nao muda depois que a solicitacao e registrada.
 *
 * @param tipoCreditoInformado texto exatamente como o front-end enviou (ex.: "EMPRÉSTIMO PESSOAL"),
 *                             devolvido sem alteracao nas respostas, como no sistema legado
 */
public record PropostaCredito(
        Cpf cpf,
        String nomeCliente,
        String tipoCreditoInformado,
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
        TipoCredito.de(tipoCreditoInformado);
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
        tipoCreditoInformado = tipoCreditoInformado.trim();
    }

    /**
     * Tipo de credito reconhecido a partir do texto informado.
     */
    public TipoCredito tipoCredito() {
        return TipoCredito.de(tipoCreditoInformado);
    }

    /**
     * Parcela sem juros, usada pelo motor de credito para medir o comprometimento da renda.
     */
    public Dinheiro parcelaEstimada() {
        return valorSolicitado.dividir(quantidadeParcelas);
    }
}
