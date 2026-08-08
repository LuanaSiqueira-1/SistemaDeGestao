package com.concessionaria.backend.dto;

import java.math.BigDecimal;

import com.concessionaria.backend.model.StatusVeiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record VeiculoCadastroRequest(

        @NotBlank(message = "A marca é obrigatória")
        String marca,

        @NotBlank(message = "O modelo é obrigatório")
        String modelo,

        @NotNull(message = "O ano é obrigatório")
        Integer ano,

        String cor,

        @PositiveOrZero(message = "A quilometragem não pode ser negativa")
        Long quilometragem,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "O status é obrigatório")
        StatusVeiculo status

) {
}