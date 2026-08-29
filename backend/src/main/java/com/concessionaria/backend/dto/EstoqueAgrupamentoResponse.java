package com.concessionaria.backend.dto;

public record EstoqueAgrupamentoResponse(
        String nome,
        Long quantidadeTotal,
        Long quantidadeDisponivel
) {
}
