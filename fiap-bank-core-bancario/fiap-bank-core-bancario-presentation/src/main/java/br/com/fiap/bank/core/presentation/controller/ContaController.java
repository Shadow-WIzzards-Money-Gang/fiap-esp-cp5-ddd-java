package br.com.fiap.bank.core.presentation.controller;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponse;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.application.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar() {
        return ResponseEntity.ok(contaService.listar());
    }

    @GetMapping("/{numeroConta}")
    public ResponseEntity<ContaResponse> buscarPorNumero(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.buscarPorNumero(numeroConta));
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<ContaResponse> buscarPorCpfDoCliente(@PathVariable String cpf) {
        return ResponseEntity.ok(contaService.buscarPorCpfDoCliente(cpf));
    }

    @PostMapping
    public ResponseEntity<ContaResponse> abrir(@Valid @RequestBody CriarContaRequest request) {
        ContaResponse conta = contaService.abrir(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{numeroConta}")
                .buildAndExpand(conta.numeroConta())
                .toUri();
        return ResponseEntity.created(location).body(conta);
    }

    @PutMapping("/{numeroConta}/saldo")
    public ResponseEntity<ContaResponse> atualizarSaldo(@PathVariable String numeroConta,
                                                        @Valid @RequestBody AtualizarSaldoRequest request) {
        return ResponseEntity.ok(contaService.atualizarSaldo(numeroConta, request));
    }

    @DeleteMapping("/{numeroConta}")
    public ResponseEntity<Void> encerrar(@PathVariable String numeroConta) {
        contaService.encerrar(numeroConta);
        return ResponseEntity.noContent().build();
    }
}
