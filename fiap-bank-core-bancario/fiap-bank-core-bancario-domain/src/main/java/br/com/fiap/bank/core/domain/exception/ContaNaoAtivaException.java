package br.com.fiap.bank.core.domain.exception;

import br.com.fiap.bank.core.domain.enums.StatusConta;

public class ContaNaoAtivaException extends DominioException {

    public ContaNaoAtivaException(String numeroConta, StatusConta status) {
        super("Conta " + numeroConta + " nao esta ativa (status atual: " + status + ").");
    }
}
