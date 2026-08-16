package com.concessionaria.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaListagemResponse(
        Long id,
        LocalDate dataVenda,
        BigDecimal valor,
        ClienteListagemResponse cliente,
        VeiculoListagemResponse veiculo
) {
}