package br.com.fiap.bank.plataforma.domain.valueobject;

import br.com.fiap.bank.plataforma.domain.enums.TipoCredito;
import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropostaCreditoTest {

    @Test
    void calculaParcelaEstimadaSemJuros() {
        PropostaCredito proposta = proposta("350000.00", 180);

        assertThat(proposta.parcelaEstimada()).isEqualTo(Dinheiro.de("1944.44"));
    }

    @Test
    void rejeitaQuantidadeDeParcelasZero() {
        assertThatThrownBy(() -> proposta("15000.00", 0))
                .isInstanceOf(ValorInvalidoException.class)
                .hasMessageContaining("parcelas");
    }

    @Test
    void rejeitaValorSolicitadoZero() {
        assertThatThrownBy(() -> proposta("0.00", 24))
                .isInstanceOf(ValorInvalidoException.class)
                .hasMessageContaining("Valor solicitado");
    }

    @Test
    void guardaOTextoDoTipoComoFoiEnviadoEExpoeOEnum() {
        PropostaCredito proposta = new PropostaCredito(new Cpf("98765432100"), "Carlos Eduardo Oliveira",
                " CRÉDITO IMOBILIÁRIO ", Dinheiro.de("1000.00"), 10, Dinheiro.de("12000.00"));

        assertThat(proposta.tipoCreditoInformado()).isEqualTo("CRÉDITO IMOBILIÁRIO");
        assertThat(proposta.tipoCredito()).isEqualTo(TipoCredito.CREDITO_IMOBILIARIO);
    }

    @Test
    void rejeitaTipoDeCreditoDesconhecido() {
        assertThatThrownBy(() -> new PropostaCredito(new Cpf("98765432100"), "Carlos Eduardo Oliveira",
                "CHEQUE_ESPECIAL", Dinheiro.de("1000.00"), 10, Dinheiro.de("12000.00")))
                .isInstanceOf(ValorInvalidoException.class);
    }

    @Test
    void cpfAceitaMascaraENormalizaParaOnzeDigitos() {
        assertThat(new Cpf("123.456.789-00").valor()).isEqualTo("12345678900");
        assertThatThrownBy(() -> new Cpf("1234")).isInstanceOf(ValorInvalidoException.class);
    }

    private static PropostaCredito proposta(String valor, int parcelas) {
        return new PropostaCredito(new Cpf("98765432100"), "Carlos Eduardo Oliveira",
                "CREDITO_IMOBILIARIO", Dinheiro.de(valor), parcelas, Dinheiro.de("12000.00"));
    }
}
