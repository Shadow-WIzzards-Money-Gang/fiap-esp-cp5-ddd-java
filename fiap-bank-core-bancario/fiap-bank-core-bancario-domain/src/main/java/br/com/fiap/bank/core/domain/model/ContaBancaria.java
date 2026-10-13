package br.com.fiap.bank.core.domain.model;

import br.com.fiap.bank.core.domain.enums.StatusConta;
import br.com.fiap.bank.core.domain.enums.TipoConta;
import br.com.fiap.bank.core.domain.exception.ContaNaoAtivaException;
import br.com.fiap.bank.core.domain.exception.ValorInvalidoException;
import br.com.fiap.bank.core.domain.valueobject.Saldo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContaBancaria {

    public static final String AGENCIA_PADRAO = "0001";

    @EqualsAndHashCode.Include
    private final UUID id;
    private final String numero;
    private final String agencia;
    private Saldo saldo;
    private final LocalDate dataAbertura;
    private StatusConta status;
    private final TipoConta tipo;

    /**
     * Abre uma conta nova: ativa, com data de abertura de hoje e agencia padrao quando nao informada.
     */
    public static ContaBancaria abrir(String numero, String agencia, TipoConta tipo, Saldo saldoInicial) {
        String agenciaDaConta = (agencia == null || agencia.isBlank()) ? AGENCIA_PADRAO : agencia;
        return reconstituir(UUID.randomUUID(), numero, agenciaDaConta, saldoInicial,
                LocalDate.now(), StatusConta.ATIVA, tipo);
    }

    /**
     * Recria uma conta ja existente (ex.: vinda da persistencia), preservando seu estado.
     */
    public static ContaBancaria reconstituir(UUID id, String numero, String agencia, Saldo saldo,
                                             LocalDate dataAbertura, StatusConta status, TipoConta tipo) {
        if (id == null) {
            throw new ValorInvalidoException("Id da conta e obrigatorio.");
        }
        if (numero == null || numero.isBlank()) {
            throw new ValorInvalidoException("Numero da conta e obrigatorio.");
        }
        if (agencia == null || !agencia.matches("\\d{4}")) {
            throw new ValorInvalidoException("Agencia deve conter 4 digitos.");
        }
        if (saldo == null) {
            throw new ValorInvalidoException("Saldo da conta e obrigatorio.");
        }
        if (dataAbertura == null) {
            throw new ValorInvalidoException("Data de abertura da conta e obrigatoria.");
        }
        if (status == null) {
            throw new ValorInvalidoException("Status da conta e obrigatorio.");
        }
        if (tipo == null) {
            throw new ValorInvalidoException("Tipo da conta e obrigatorio.");
        }
        return new ContaBancaria(id, numero, agencia, saldo, dataAbertura, status, tipo);
    }

    public void depositar(BigDecimal valor) {
        garantirAtiva();
        this.saldo = saldo.adicionar(valor);
    }

    public void sacar(BigDecimal valor) {
        garantirAtiva();
        this.saldo = saldo.subtrair(valor);
    }

    public void encerrar() {
        this.status = StatusConta.ENCERRADA;
    }

    public boolean estaAtiva() {
        return status == StatusConta.ATIVA;
    }

    private void garantirAtiva() {
        if (!estaAtiva()) {
            throw new ContaNaoAtivaException(numero, status);
        }
    }
}
