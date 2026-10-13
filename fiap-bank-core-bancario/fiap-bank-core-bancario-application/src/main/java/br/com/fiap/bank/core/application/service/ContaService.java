package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponse;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.application.exception.RecursoDuplicadoException;
import br.com.fiap.bank.core.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.domain.repository.ContaBancariaRepository;
import br.com.fiap.bank.core.domain.valueobject.Cpf;
import br.com.fiap.bank.core.domain.valueobject.Saldo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class ContaService {

    private final ContaBancariaRepository contaBancariaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ContaResponse> listar() {
        return contaBancariaRepository.listarTodas().stream().map(ContaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorNumero(String numeroConta) {
        return ContaResponse.de(obterPorNumero(numeroConta));
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorCpfDoCliente(String cpf) {
        Cpf cpfDoCliente = TradutorExcecaoDominio.obter(() -> new Cpf(cpf));
        return contaBancariaRepository.buscarPorCpfDoCliente(cpfDoCliente)
                .map(ContaResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Conta do cliente com CPF " + cpfDoCliente + " nao encontrada."));
    }

    public ContaResponse abrir(CriarContaRequest request) {
        Cpf cpfDoCliente = TradutorExcecaoDominio.obter(() -> new Cpf(request.cpfCliente()));
        Cliente cliente = clienteRepository.buscarPorCpf(cpfDoCliente)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente com CPF " + cpfDoCliente + " nao encontrado."));

        if (cliente.possuiConta()) {
            throw new RecursoDuplicadoException("Cliente com CPF " + cpfDoCliente + " ja possui a conta "
                    + cliente.getConta().getNumero() + ".");
        }

        TradutorExcecaoDominio.executar(() -> cliente.vincularConta(ContaBancaria.abrir(
                gerarNumeroDisponivel(),
                request.agencia(),
                request.tipoConta(),
                new Saldo(request.saldoInicial()))));

        return ContaResponse.de(clienteRepository.salvar(cliente).getConta());
    }

    public ContaResponse atualizarSaldo(String numeroConta, AtualizarSaldoRequest request) {
        ContaBancaria conta = obterPorNumero(numeroConta);
        OperacaoSaldo operacao = OperacaoSaldo.de(request.operacao());
        TradutorExcecaoDominio.executar(() -> operacao.aplicar(conta, request.valor()));

        return ContaResponse.de(contaBancariaRepository.salvar(conta));
    }

    /**
     * Inativacao logica e idempotente: encerrar uma conta ja encerrada nao tem efeito.
     */
    public void encerrar(String numeroConta) {
        ContaBancaria conta = obterPorNumero(numeroConta);
        conta.encerrar();
        contaBancariaRepository.salvar(conta);
    }

    private ContaBancaria obterPorNumero(String numeroConta) {
        return contaBancariaRepository.buscarPorNumero(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta " + numeroConta + " nao encontrada."));
    }

    /** Sorteia numeros no formato NNNNN-D ate achar um ainda nao usado. */
    private String gerarNumeroDisponivel() {
        ThreadLocalRandom sorteio = ThreadLocalRandom.current();
        String numero;
        do {
            numero = "%05d-%d".formatted(sorteio.nextInt(100_000), sorteio.nextInt(10));
        } while (contaBancariaRepository.existePorNumero(numero));
        return numero;
    }
}
