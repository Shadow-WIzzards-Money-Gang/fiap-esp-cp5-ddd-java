package br.com.fiap.bank.core.infrastructure.config;

import br.com.fiap.bank.core.domain.enums.TipoConta;
import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.domain.valueobject.Cpf;
import br.com.fiap.bank.core.domain.valueobject.Email;
import br.com.fiap.bank.core.domain.valueobject.Saldo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Carga inicial do Core Bancario. Os CPFs, nomes, rendas e saldos devem ficar
 * consistentes com os dados usados pela Plataforma de Credito.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    @Override
    public void run(String... args) {
        cadastrarComConta("Ana Silva", "ana.silva@fiapbank.com.br", LocalDate.of(1990, 3, 15),
                "12345678900", "7500.00", "12345-6", "12500.00");
        cadastrarComConta("Carlos Eduardo Oliveira", "carlos.oliveira@fiapbank.com.br", LocalDate.of(1985, 8, 22),
                "98765432100", "12000.00", "78901-2", "45800.50");
        // Mariana tem restricao no SCR e nao possui conta no Core
        cadastrarSemConta("Mariana Souza", "mariana.souza@fiapbank.com.br", LocalDate.of(1995, 11, 5),
                "55544433399", "4200.00");
    }

    private void cadastrarComConta(String nome, String email, LocalDate dataNascimento, String cpf,
                                   String rendaMensal, String numeroConta, String saldoInicial) {
        Cliente cliente = novoCliente(nome, email, dataNascimento, cpf, rendaMensal);
        cliente.vincularConta(ContaBancaria.abrir(numeroConta, ContaBancaria.AGENCIA_PADRAO, TipoConta.CORRENTE,
                new Saldo(new BigDecimal(saldoInicial))));
        salvarSeNaoExistir(cliente);
    }

    private void cadastrarSemConta(String nome, String email, LocalDate dataNascimento, String cpf,
                                   String rendaMensal) {
        salvarSeNaoExistir(novoCliente(nome, email, dataNascimento, cpf, rendaMensal));
    }

    private Cliente novoCliente(String nome, String email, LocalDate dataNascimento, String cpf,
                                String rendaMensal) {
        return Cliente.cadastrar(nome, new Email(email), dataNascimento, new Cpf(cpf), new BigDecimal(rendaMensal));
    }

    private void salvarSeNaoExistir(Cliente cliente) {
        if (clienteRepository.existePorCpf(cliente.getCpf())) {
            log.info("Carga inicial: cliente com CPF {} ja existe, mantido como esta.", cliente.getCpf());
            return;
        }
        clienteRepository.salvar(cliente);
        log.info("Carga inicial: cliente {} (CPF {}) cadastrado{}.", cliente.getNome(), cliente.getCpf(),
                cliente.possuiConta() ? " com a conta " + cliente.getConta().getNumero() : " sem conta");
    }
}
