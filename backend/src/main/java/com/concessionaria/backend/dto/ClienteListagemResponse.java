package com.concessionaria.backend.dto;

public record ClienteListagemResponse(
        Long id,
        String nome,
        String cpf
) {
}