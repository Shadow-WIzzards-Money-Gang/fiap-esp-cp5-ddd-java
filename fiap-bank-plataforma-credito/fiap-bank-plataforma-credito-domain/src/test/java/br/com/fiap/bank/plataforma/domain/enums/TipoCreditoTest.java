package br.com.fiap.bank.plataforma.domain.enums;

import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoCreditoTest {

    @ParameterizedTest
    @CsvSource({
            "EMPRESTIMO_PESSOAL, EMPRESTIMO_PESSOAL",
            "EMPRÉSTIMO PESSOAL, EMPRESTIMO_PESSOAL",
            "CRÉDITO IMOBILIÁRIO, CREDITO_IMOBILIARIO",
            "crédito automotivo, CREDITO_AUTOMOTIVO",
            "CONSIGNADO, CONSIGNADO"
    })
    void aceitaNomesDoEnumEOsEnviadosPelosFrontEndsLegados(String valor, TipoCredito esperado) {
        assertThat(TipoCredito.de(valor)).isEqualTo(esperado);
    }

    @Test
    void rejeitaTipoDesconhecido() {
        assertThatThrownBy(() -> TipoCredito.de("CHEQUE_ESPECIAL"))
                .isInstanceOf(ValorInvalidoException.class);
    }
}
