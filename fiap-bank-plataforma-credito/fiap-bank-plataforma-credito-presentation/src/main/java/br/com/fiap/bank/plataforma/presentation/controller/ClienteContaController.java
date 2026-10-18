package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.response.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClienteContaController {

    private final SolicitacaoCreditoService solicitacaoCreditoService;

    @GetMapping("/{cpf}/conta")
    public ResponseEntity<ContaClienteResponse> buscarConta(@PathVariable String cpf) {
        return ResponseEntity.ok(solicitacaoCreditoService.buscarContaCliente(cpf));
    }
}
