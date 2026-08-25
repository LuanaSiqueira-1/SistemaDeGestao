package com.concessionaria.backend.exception;

public class VeiculoNaoEncontradoException extends RuntimeException {

    public VeiculoNaoEncontradoException(Long id) {
        super("Veículo não encontrado com o ID: " + id);
    }
}