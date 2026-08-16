package com.concessionaria.backend.exception;

public class VendaNaoEncontradaException extends RuntimeException{

    public VendaNaoEncontradaException(Long id){
        super("Venda não encontrada com o ID: "+ id);
    }
}
