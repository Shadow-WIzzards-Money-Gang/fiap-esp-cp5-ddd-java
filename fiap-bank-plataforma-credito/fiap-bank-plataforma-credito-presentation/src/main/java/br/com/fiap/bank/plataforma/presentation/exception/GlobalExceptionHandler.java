package br.com.fiap.bank.plataforma.presentation.exception;

import br.com.fiap.bank.plataforma.application.exception.IntegracaoIndisponivelException;
import br.com.fiap.bank.plataforma.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.bank.plataforma.application.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarCorpoInvalido(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request) {
        List<CampoErro> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new CampoErro(erro.getField(), erro.getDefaultMessage()))
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Dados da requisicao invalidos.", request, campos);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> tratarParametroInvalido(ConstraintViolationException ex,
                                                                HttpServletRequest request) {
        List<CampoErro> campos = ex.getConstraintViolations().stream()
                .map(violacao -> new CampoErro(violacao.getPropertyPath().toString(), violacao.getMessage()))
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Dados da requisicao invalidos.", request, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarCorpoIlegivel(HttpMessageNotReadableException ex,
                                                            HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "Corpo da requisicao ausente ou malformado.", request, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarParametroComTipoErrado(MethodArgumentTypeMismatchException ex,
                                                                     HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "Valor invalido para o parametro '" + ex.getName() + "'.",
                request, List.of());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraDeNegocio(RegraNegocioException ex, HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex,
                                                            HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(IntegracaoIndisponivelException.class)
    public ResponseEntity<ErroResponse> tratarIntegracaoIndisponivel(IntegracaoIndisponivelException ex,
                                                                     HttpServletRequest request) {
        log.error("Falha de integracao em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return responder(HttpStatus.BAD_GATEWAY, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception ex, HttpServletRequest request) {
        // Excecoes do proprio Spring MVC (rota inexistente, metodo nao suportado...) ja trazem o status certo
        if (ex instanceof ErrorResponse erroDoSpring) {
            HttpStatusCode status = erroDoSpring.getStatusCode();
            return responder(status, "Requisicao nao pode ser atendida: " + descrever(status) + ".",
                    request, List.of());
        }
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente mais tarde.",
                request, List.of());
    }

    private ResponseEntity<ErroResponse> responder(HttpStatusCode status, String mensagem,
                                                   HttpServletRequest request, List<CampoErro> campos) {
        ErroResponse corpo = new ErroResponse(OffsetDateTime.now(), status.value(), descrever(status), mensagem,
                request.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }

    private String descrever(HttpStatusCode status) {
        HttpStatus conhecido = HttpStatus.resolve(status.value());
        return conhecido != null ? conhecido.getReasonPhrase() : String.valueOf(status.value());
    }
}
