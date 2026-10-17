package br.com.fiap.bank.plataforma.domain.enums;

import br.com.fiap.bank.plataforma.domain.exception.ValorInvalidoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum TipoCredito {
    EMPRESTIMO_PESSOAL("Empréstimo Pessoal"),
    CREDITO_IMOBILIARIO("Crédito Imobiliário"),
    CREDITO_AUTOMOTIVO("Crédito Automotivo"),
    CONSIGNADO("Consignado");

    private final String descricao;

    /**
     * Aceita o nome do enum e tambem as variacoes enviadas pelos front-ends legados,
     * com acento, espaco ou caixa diferente (ex.: "EMPRÉSTIMO PESSOAL").
     */
    public static TipoCredito de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("Tipo de credito e obrigatorio.");
        }
        String normalizado = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]+", "_")
                .toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(tipo -> tipo.name().equals(normalizado))
                .findFirst()
                .orElseThrow(() -> new ValorInvalidoException("Tipo de credito invalido: " + valor
                        + ". Valores aceitos: " + Arrays.stream(values()).map(Enum::name)
                        .collect(Collectors.joining(", ")) + "."));
    }
}
