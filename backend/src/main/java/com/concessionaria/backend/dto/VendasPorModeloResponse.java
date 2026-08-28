package com.concessionaria.backend.dto;

import java.math.BigDecimal;

public record VendasPorModeloResponse(
        String marca,
        String modelo,
        long quantidade,
        BigDecimal valorTotal
) {
}
