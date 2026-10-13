package br.com.fiap.bank.core.domain.model;

import br.com.fiap.bank.core.domain.exception.DominioException;
import br.com.fiap.bank.core.domain.exception.ValorInvalidoException;
import br.com.fiap.bank.core.domain.valueobject.Cpf;
import br.com.fiap.bank.core.domain.valueobject.Email;
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
public class Cliente {

    private static final int NOME_TAMANHO_MINIMO = 3;
    private static final int NOME_TAMANHO_MAXIMO = 150;

    @EqualsAndHashCode.Include
    private final UUID id;
    private String nome;
    private Email email;
    private LocalDate dataNascimento;
    private final Cpf cpf;
    private BigDecimal rendaMensal;
    /** Nulo enquanto o cliente ainda nao abriu conta. */
    private ContaBancaria conta;

    /**
     * Cadastra um cliente novo, ainda sem conta.
     */
    public static Cliente cadastrar(String nome, Email email, LocalDate dataNascimento, Cpf cpf,
                                    BigDecimal rendaMensal) {
        return reconstituir(UUID.randomUUID(), nome, email, dataNascimento, cpf, rendaMensal, null);
    }

    /**
     * Recria um cliente ja existente (ex.: vindo da persistencia), preservando seu estado.
     */
    public static Cliente reconstituir(UUID id, String nome, Email email, LocalDate dataNascimento, Cpf cpf,
                                       BigDecimal rendaMensal, ContaBancaria conta) {
        if (id == null) {
            throw new ValorInvalidoException("Id do cliente e obrigatorio.");
        }
        if (cpf == null) {
            throw new ValorInvalidoException("CPF do cliente e obrigatorio.");
        }
        validarDadosCadastrais(nome, email, dataNascimento, rendaMensal);
        return new Cliente(id, nome.trim(), email, dataNascimento, cpf, rendaMensal, conta);
    }

    /**
     * Atualiza os dados cadastrais. O CPF identifica o cliente e nao pode ser alterado.
     */
    public void atualizarDadosCadastrais(String nome, Email email, LocalDate dataNascimento,
                                         BigDecimal rendaMensal) {
        validarDadosCadastrais(nome, email, dataNascimento, rendaMensal);
        this.nome = nome.trim();
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.rendaMensal = rendaMensal;
    }

    public void vincularConta(ContaBancaria conta) {
        if (conta == null) {
            throw new ValorInvalidoException("Conta e obrigatoria.");
        }
        if (possuiConta()) {
            throw new DominioException("Cliente ja possui a conta " + this.conta.getNumero() + ".");
        }
        this.conta = conta;
    }

    public boolean possuiConta() {
        return conta != null;
    }

    private static void validarDadosCadastrais(String nome, Email email, LocalDate dataNascimento,
                                               BigDecimal rendaMensal) {
        if (nome == null || nome.isBlank()) {
            throw new ValorInvalidoException("Nome e obrigatorio.");
        }
        int tamanho = nome.trim().length();
        if (tamanho < NOME_TAMANHO_MINIMO || tamanho > NOME_TAMANHO_MAXIMO) {
            throw new ValorInvalidoException("Nome deve ter entre " + NOME_TAMANHO_MINIMO + " e "
                    + NOME_TAMANHO_MAXIMO + " caracteres.");
        }
        if (email == null) {
            throw new ValorInvalidoException("E-mail e obrigatorio.");
        }
        if (dataNascimento == null || !dataNascimento.isBefore(LocalDate.now())) {
            throw new ValorInvalidoException("Data de nascimento deve estar no passado.");
        }
        if (rendaMensal == null || rendaMensal.signum() < 0) {
            throw new ValorInvalidoException("Renda mensal nao pode ser negativa.");
        }
    }
}
