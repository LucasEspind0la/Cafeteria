package br.com.cafeteria.api.rest.advice; // ← PACOTE CORRIGIDO

import br.com.cafeteria.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções - SRP
 * Centraliza o tratamento de TODOS os erros da API em um só lugar
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProdutoNaoEncontrado(ProdutoNaoEncontradoException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleEstoqueInsuficiente(EstoqueInsuficienteException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoNaoEncontrado(PedidoNaoEncontradoException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // ✅ Padronizado para retornar ErrorResponse (consistência na API)
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Erro de validação", HttpStatus.BAD_REQUEST.value(), errors, LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return buildResponse("Erro interno do servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(message, status.value(), null, LocalDateTime.now()));
    }

    // ✅ Record atualizado para incluir os erros de validação
    public record ErrorResponse(
            String message,
            int status,
            Map<String, String> errors,  // ← Campo adicionado para detalhes de validação
            LocalDateTime timestamp
    ) {
        // Construtor de conveniência para erros sem detalhes
        public ErrorResponse(String message, int status, LocalDateTime timestamp) {
            this(message, status, null, timestamp);
        }
    }
}