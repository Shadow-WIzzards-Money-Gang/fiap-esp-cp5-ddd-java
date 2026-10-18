package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.request.AtualizarClienteRequest;
import br.com.fiap.bank.core.application.dto.request.CriarClienteRequest;
import br.com.fiap.bank.core.application.dto.response.ClienteResponse;
import br.com.fiap.bank.core.application.exception.RecursoDuplicadoException;
import br.com.fiap.bank.core.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.domain.valueobject.Cpf;
import br.com.fiap.bank.core.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.listarTodos().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(UUID id) {
        return ClienteResponse.de(obterPorId(id));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorCpf(String cpf) {
        Cpf cpfDoCliente = TradutorExcecaoDominio.obter(() -> new Cpf(cpf));
        return clienteRepository.buscarPorCpf(cpfDoCliente)
                .map(ClienteResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente com CPF " + cpfDoCliente + " nao encontrado."));
    }

    public ClienteResponse criar(CriarClienteRequest request) {
        Cliente cliente = TradutorExcecaoDominio.obter(() -> Cliente.cadastrar(
                request.nome(),
                new Email(request.email()),
                request.dataNascimento(),
                new Cpf(request.cpf()),
                request.rendaMensal()));

        if (clienteRepository.existePorCpf(cliente.getCpf())) {
            throw new RecursoDuplicadoException("Ja existe cliente com o CPF " + cliente.getCpf() + ".");
        }
        garantirEmailDisponivel(cliente);

        return ClienteResponse.de(clienteRepository.salvar(cliente));
    }

    public ClienteResponse atualizar(UUID id, AtualizarClienteRequest request) {
        Cliente cliente = obterPorId(id);
        TradutorExcecaoDominio.executar(() -> cliente.atualizarDadosCadastrais(
                request.nome(),
                new Email(request.email()),
                request.dataNascimento(),
                request.rendaMensal()));
        garantirEmailDisponivel(cliente);

        return ClienteResponse.de(clienteRepository.salvar(cliente));
    }

    public void remover(UUID id) {
        clienteRepository.remover(obterPorId(id));
    }

    private Cliente obterPorId(UUID id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + id + " nao encontrado."));
    }

    private void garantirEmailDisponivel(Cliente cliente) {
        clienteRepository.buscarPorEmail(cliente.getEmail())
                .filter(outro -> !outro.equals(cliente))
                .ifPresent(outro -> {
                    throw new RecursoDuplicadoException(
                            "Ja existe cliente com o e-mail " + cliente.getEmail() + ".");
                });
    }
}
