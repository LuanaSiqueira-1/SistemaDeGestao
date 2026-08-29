package com.concessionaria.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.concessionaria.backend.dto.EstoqueAgrupamentoResponse;
import com.concessionaria.backend.dto.EstoqueResumoResponse;
import com.concessionaria.backend.exception.StatusEstoqueInvalidoException;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.repository.VeiculoRepository;

@Service
public class EstoqueService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private static final BigDecimal LIMITE_50000 =
            new BigDecimal("50000.00");

    private static final BigDecimal LIMITE_100000 =
            new BigDecimal("100000.00");

    private static final BigDecimal LIMITE_150000 =
            new BigDecimal("150000.00");

    private final VeiculoRepository veiculoRepository;

    public EstoqueService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public EstoqueResumoResponse resumir(
            String marca,
            String status
    ) {
        StatusVeiculo statusFiltrado = converterStatus(status);

        List<Veiculo> veiculos = veiculoRepository.findAll()
                .stream()
                .filter(veiculo -> marca == null
                        || marca.isBlank()
                        || veiculo.getMarca().equalsIgnoreCase(marca))
                .filter(veiculo -> statusFiltrado == null
                        || veiculo.getStatus() == statusFiltrado)
                .toList();

        long quantidadeTotal = veiculos.size();

        long quantidadeDisponivel = veiculos.stream()
                .filter(this::estaDisponivel)
                .count();

        long quantidadeIndisponivel =
                quantidadeTotal - quantidadeDisponivel;

        BigDecimal percentualDisponivel =
                calcularPercentual(
                        quantidadeDisponivel,
                        quantidadeTotal
                );

        BigDecimal valorTotalDisponivel = veiculos.stream()
                .filter(this::estaDisponivel)
                .map(Veiculo::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new EstoqueResumoResponse(
                quantidadeTotal,
                quantidadeDisponivel,
                quantidadeIndisponivel,
                percentualDisponivel,
                valorTotalDisponivel,
                agrupar(
                        veiculos,
                        Veiculo::getMarca
                ),
                agrupar(
                        veiculos,
                        Veiculo::getModelo
                ),
                agrupar(
                        veiculos,
                        veiculo -> faixaPreco(veiculo.getPreco())
                )
        );
    }

    private StatusVeiculo converterStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return StatusVeiculo.valueOf(
                    status.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new StatusEstoqueInvalidoException(status);
        }
    }

    private boolean estaDisponivel(Veiculo veiculo) {
        return veiculo.getStatus() == StatusVeiculo.DISPONIVEL;
    }

    private BigDecimal calcularPercentual(
            long quantidadeDisponivel,
            long quantidadeTotal
    ) {
        if (quantidadeTotal == 0) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return BigDecimal.valueOf(quantidadeDisponivel)
                .multiply(CEM)
                .divide(
                        BigDecimal.valueOf(quantidadeTotal),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private String faixaPreco(BigDecimal preco) {
        if (preco.compareTo(LIMITE_50000) <= 0) {
            return "ATE_50000";
        }

        if (preco.compareTo(LIMITE_100000) <= 0) {
            return "DE_50000_A_100000";
        }

        if (preco.compareTo(LIMITE_150000) <= 0) {
            return "DE_100000_A_150000";
        }

        return "ACIMA_150000";
    }

    private List<EstoqueAgrupamentoResponse> agrupar(
            List<Veiculo> veiculos,
            Function<Veiculo, String> agrupador
    ) {
        Map<String, List<Veiculo>> grupos = veiculos.stream()
                .collect(Collectors.groupingBy(
                        agrupador,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return grupos.entrySet()
                .stream()
                .map(entry -> {
                    long total = entry.getValue().size();

                    long disponiveis = entry.getValue()
                            .stream()
                            .filter(this::estaDisponivel)
                            .count();

                    return new EstoqueAgrupamentoResponse(
                            entry.getKey(),
                            total,
                            disponiveis
                    );
                })
                .sorted(
                        Comparator
                                .comparing(
                                        EstoqueAgrupamentoResponse
                                                ::quantidadeTotal
                                )
                                .reversed()
                                .thenComparing(
                                        EstoqueAgrupamentoResponse::nome
                                )
                )
                .toList();
    }
}