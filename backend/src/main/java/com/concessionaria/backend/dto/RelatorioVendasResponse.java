package com.concessionaria.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioVendasResponse(
        int ano,
        Integer semestre,
        long quantidadeVendas,
        BigDecimal valorTotal,
        BigDecimal ticketMedio,
        List<VendasPorMarcaResponse> vendasPorMarca,
        List<VendasPorModeloResponse> vendasPorModelo,
        List<RankingModeloResponse> maisVendidos,
        List<RankingModeloResponse> menosVendidos
) {
}
