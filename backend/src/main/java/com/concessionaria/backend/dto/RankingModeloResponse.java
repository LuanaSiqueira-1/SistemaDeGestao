package com.concessionaria.backend.dto;

public record RankingModeloResponse(
        String marca,
        String modelo,
        long quantidade
) {
}
