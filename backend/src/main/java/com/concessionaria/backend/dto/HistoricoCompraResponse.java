package com.concessionaria.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricoCompraResponse(
        VeiculoHistoricoResponse veiculo,
        LocalDate dataVenda,
        BigDecimal valor
) {
    public record VeiculoHistoricoResponse(
            Long id,
            String marca,
            String modelo,
            Integer ano
    ) {
    }
}
