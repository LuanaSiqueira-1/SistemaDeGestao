package com.concessionaria.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.concessionaria.backend.dto.RankingModeloResponse;
import com.concessionaria.backend.dto.RelatorioVendasResponse;
import com.concessionaria.backend.dto.VendasPorMarcaResponse;
import com.concessionaria.backend.dto.VendasPorModeloResponse;
import com.concessionaria.backend.model.Venda;
import com.concessionaria.backend.repository.VendaRepository;

@Service
public class RelatorioVendasService {

    private static final BigDecimal ZERO_MONETARIO =
            new BigDecimal("0.00");

    private static final Comparator<String> ORDEM_TEXTO =
            String.CASE_INSENSITIVE_ORDER.thenComparing(
                    Comparator.naturalOrder()
            );

    private final VendaRepository vendaRepository;

    public RelatorioVendasService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    @Transactional(readOnly = true)
    public RelatorioVendasResponse gerar(
            int ano,
            Integer semestre,
            String marca
    ) {
        validarParametros(ano, semestre);

        String marcaNormalizada = marca == null ? "" : marca.trim();
        LocalDate dataInicio = calcularDataInicio(ano, semestre);
        LocalDate dataFim = calcularDataFim(ano, semestre);

        List<Venda> vendas = vendaRepository.buscarParaRelatorio(
                dataInicio,
                dataFim,
                marcaNormalizada
        );

        long quantidadeVendas = vendas.size();
        BigDecimal valorTotal = vendas.stream()
                .map(Venda::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal ticketMedio = quantidadeVendas == 0
                ? ZERO_MONETARIO
                : valorTotal.divide(
                        BigDecimal.valueOf(quantidadeVendas),
                        2,
                        RoundingMode.HALF_UP
                );

        List<VendasPorMarcaResponse> vendasPorMarca =
                agruparPorMarca(vendas);
        List<VendasPorModeloResponse> vendasPorModelo =
                agruparPorModelo(vendas);

        return new RelatorioVendasResponse(
                ano,
                semestre,
                quantidadeVendas,
                valorTotal,
                ticketMedio,
                vendasPorMarca,
                vendasPorModelo,
                obterMaisVendidos(vendasPorModelo),
                obterMenosVendidos(vendasPorModelo)
        );
    }

    private void validarParametros(int ano, Integer semestre) {
        if (ano <= 0) {
            throw new IllegalArgumentException(
                    "O ano deve ser maior que zero."
            );
        }

        if (semestre != null && semestre != 1 && semestre != 2) {
            throw new IllegalArgumentException(
                    "O semestre deve ser 1 ou 2."
            );
        }
    }

    private LocalDate calcularDataInicio(int ano, Integer semestre) {
        if (semestre == null || semestre == 1) {
            return LocalDate.of(ano, 1, 1);
        }

        return LocalDate.of(ano, 7, 1);
    }

    private LocalDate calcularDataFim(int ano, Integer semestre) {
        if (semestre != null && semestre == 1) {
            return LocalDate.of(ano, 7, 1);
        }

        return LocalDate.of(ano + 1, 1, 1);
    }

    private List<VendasPorMarcaResponse> agruparPorMarca(
            List<Venda> vendas
    ) {
        Map<String, Acumulador> acumuladores = new LinkedHashMap<>();

        for (Venda venda : vendas) {
            acumuladores.computeIfAbsent(
                    venda.getVeiculo().getMarca(),
                    chave -> new Acumulador()
            ).adicionar(venda.getValor());
        }

        return acumuladores.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(ORDEM_TEXTO))
                .map(entrada -> new VendasPorMarcaResponse(
                        entrada.getKey(),
                        entrada.getValue().quantidade,
                        entrada.getValue().valorTotal
                                .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    private List<VendasPorModeloResponse> agruparPorModelo(
            List<Venda> vendas
    ) {
        Map<ChaveModelo, Acumulador> acumuladores =
                new LinkedHashMap<>();

        for (Venda venda : vendas) {
            ChaveModelo chave = new ChaveModelo(
                    venda.getVeiculo().getMarca(),
                    venda.getVeiculo().getModelo()
            );

            acumuladores.computeIfAbsent(
                    chave,
                    valor -> new Acumulador()
            ).adicionar(venda.getValor());
        }

        return acumuladores.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(
                        Comparator.comparing(
                                ChaveModelo::marca,
                                ORDEM_TEXTO
                        ).thenComparing(
                                ChaveModelo::modelo,
                                ORDEM_TEXTO
                        )
                ))
                .map(entrada -> new VendasPorModeloResponse(
                        entrada.getKey().marca(),
                        entrada.getKey().modelo(),
                        entrada.getValue().quantidade,
                        entrada.getValue().valorTotal
                                .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    private List<RankingModeloResponse> obterMaisVendidos(
            List<VendasPorModeloResponse> vendasPorModelo
    ) {
        long maiorQuantidade = vendasPorModelo.stream()
                .mapToLong(VendasPorModeloResponse::quantidade)
                .max()
                .orElse(0);

        return filtrarRanking(vendasPorModelo, maiorQuantidade);
    }

    private List<RankingModeloResponse> obterMenosVendidos(
            List<VendasPorModeloResponse> vendasPorModelo
    ) {
        long menorQuantidade = vendasPorModelo.stream()
                .mapToLong(VendasPorModeloResponse::quantidade)
                .min()
                .orElse(0);

        return filtrarRanking(vendasPorModelo, menorQuantidade);
    }

    private List<RankingModeloResponse> filtrarRanking(
            List<VendasPorModeloResponse> vendasPorModelo,
            long quantidade
    ) {
        if (quantidade == 0) {
            return List.of();
        }

        List<RankingModeloResponse> ranking = new ArrayList<>();

        for (VendasPorModeloResponse vendas : vendasPorModelo) {
            if (vendas.quantidade() == quantidade) {
                ranking.add(new RankingModeloResponse(
                        vendas.marca(),
                        vendas.modelo(),
                        vendas.quantidade()
                ));
            }
        }

        return List.copyOf(ranking);
    }

    private record ChaveModelo(String marca, String modelo) {
    }

    private static class Acumulador {

        private long quantidade;
        private BigDecimal valorTotal = BigDecimal.ZERO;

        private void adicionar(BigDecimal valor) {
            quantidade++;
            valorTotal = valorTotal.add(valor);
        }
    }
}
