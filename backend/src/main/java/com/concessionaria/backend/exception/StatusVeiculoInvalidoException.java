package com.concessionaria.backend.exception;

public class StatusVeiculoInvalidoException extends RuntimeException {

    public StatusVeiculoInvalidoException(String mensagem) {
        super(mensagem);
    }
}