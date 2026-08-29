package com.concessionaria.backend.exception;

public class StatusEstoqueInvalidoException extends RuntimeException {

    public StatusEstoqueInvalidoException(String status) {
        super("Status de veículo inválido: " + status);
    }
}
