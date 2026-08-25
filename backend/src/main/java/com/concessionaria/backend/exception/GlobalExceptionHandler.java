package com.concessionaria.backend.exception;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.concessionaria.backend.dto.ErroResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock clock = Clock.systemUTC();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> campos = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> campos.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        ErroResponse resposta = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Um ou mais campos estão inválidos.",
                campos,
                LocalDateTime.now(clock)
        );

        return ResponseEntity
                .badRequest()
                .body(resposta);
    }

    @ExceptionHandler({
            ClienteNaoEncontradoException.class,
            VendaNaoEncontradaException.class,
            VeiculoNaoEncontradoException.class
    })
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado(
            RuntimeException exception
    ) {

        return criarResposta(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage()
        );
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> handleConflito(
            EmailJaCadastradoException exception
    ) {

        return criarResposta(
                HttpStatus.CONFLICT,
                "Conflito",
                exception.getMessage()
        );
    }

    @ExceptionHandler(StatusVeiculoInvalidoException.class)
    public ResponseEntity<ErroResponse> handleStatusVeiculoInvalido(
            StatusVeiculoInvalidoException exception
    ) {

        return criarResposta(
                HttpStatus.CONFLICT,
                "Conflito",
                exception.getMessage()
        );
    }

    private ResponseEntity<ErroResponse> criarResposta(
            HttpStatus status,
            String erro,
            String mensagem
    ) {

        ErroResponse resposta = new ErroResponse(
                status.value(),
                erro,
                mensagem,
                Map.of(),
                LocalDateTime.now(clock)
        );

        return ResponseEntity
                .status(status)
                .body(resposta);
    }
}