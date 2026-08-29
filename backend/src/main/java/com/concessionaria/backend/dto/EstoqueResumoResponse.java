package com.concessionaria.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record EstoqueResumoResponse(
        Long quantidadeTotal,
        Long quantidadeDisponivel,
        Long quantidadeIndisponivel,
        BigDecimal percentualDisponivel,
        BigDecimal valorTotalDisponivel,
        List<EstoqueAgrupamentoResponse> porMarca,
        List<EstoqueAgrupamentoResponse> porModelo,
        List<EstoqueAgrupamentoResponse> porFaixaPreco
) {
}
