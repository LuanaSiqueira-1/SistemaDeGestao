package com.concessionaria.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VendaRequestDTO(
    @NotNull(message = "A data da venda é obrigatória.")
    LocalDate dataVenda,

    @NotNull(message = "O valor da venda é obrigatório.")
    @Positive(message = "O valor da venda deve ser maior que zero.")
    BigDecimal valor,

    @NotNull(message = "O ID do veículo é obrigatório.")
    Long veiculoId,

    @NotNull(message = "O ID do cliente é obrigatório.")
    Long clienteId
) {
}