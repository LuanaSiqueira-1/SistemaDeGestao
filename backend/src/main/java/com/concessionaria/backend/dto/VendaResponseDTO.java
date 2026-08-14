package com.concessionaria.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.concessionaria.backend.model.Venda;

public record VendaResponseDTO(
    Long id,
    LocalDate dataVenda,
    BigDecimal valor,
    ClienteResumoDTO cliente,
    VeiculoResumoDTO veiculo
) {
    public VendaResponseDTO(Venda venda) {
        this(
            venda.getId(),
            venda.getDataVenda(),
            venda.getValor(),
            new ClienteResumoDTO(venda.getCliente().getId(), venda.getCliente().getNome()),
            new VeiculoResumoDTO(venda.getVeiculo().getId()) // Usando apenas o ID que é garantido existir!
        );
    }
}

record ClienteResumoDTO(Long id, String nome) {}
record VeiculoResumoDTO(Long id) {} // Simplificado para evitar conflito de atributos