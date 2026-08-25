package com.concessionaria.backend.dto;

import java.math.BigDecimal;

import com.concessionaria.backend.model.StatusVeiculo;

public record VeiculoDetalheResponse(
        Long id,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        Long quilometragem,
        BigDecimal preco,
        StatusVeiculo status
) {
}