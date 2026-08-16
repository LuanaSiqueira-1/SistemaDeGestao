package com.concessionaria.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaDetalheResponse(
        Long id,
        LocalDate dataVenda,
        BigDecimal valor,
        ClienteDetalheResponse cliente,
        VeiculoResponse veiculo
) {
}