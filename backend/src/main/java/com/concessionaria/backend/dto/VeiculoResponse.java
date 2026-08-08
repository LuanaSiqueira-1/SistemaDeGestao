package com.concessionaria.backend.dto;

import java.math.BigDecimal;

import com.concessionaria.backend.model.StatusVeiculo;

public record VeiculoResponse(
        Long id,
        String marca,
        String modelo,
        Integer ano,
        BigDecimal preco,
        StatusVeiculo status
) {
}