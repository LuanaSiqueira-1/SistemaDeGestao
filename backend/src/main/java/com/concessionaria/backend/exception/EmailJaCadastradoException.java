package com.concessionaria.backend.exception;

public class EmailJaCadastradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailJaCadastradoException() {
        super("E-mail já cadastrado");
    }
}