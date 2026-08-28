package com.concessionaria.backend.dto;

import java.math.BigDecimal;

public record VendasPorMarcaResponse(
        String marca,
        long quantidade,
        BigDecimal valorTotal
) {
}
