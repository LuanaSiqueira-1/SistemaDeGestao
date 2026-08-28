package com.concessionaria.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.concessionaria.backend.dto.RelatorioVendasResponse;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.model.Venda;
import com.concessionaria.backend.repository.VendaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioVendasServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @InjectMocks
    private RelatorioVendasService relatorioVendasService;

    @Test
    void deveGerarRelatorioDoAnoCompletoComAgregacoesERankings() {
        List<Venda> vendas = List.of(
                criarVenda("Toyota", "Corolla", "100000.00"),
                criarVenda("Toyota", "Corolla", "130000.00"),
                criarVenda("Volkswagen", "Polo", "100000.00"),
                criarVenda("Volkswagen", "Polo", "110000.00"),
                criarVenda("Honda", "City", "90000.00"),
                criarVenda("Toyota", "Yaris", "100000.00")
        );

        when(vendaRepository.buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                ""
        )).thenReturn(vendas);

        RelatorioVendasResponse resposta =
                relatorioVendasService.gerar(2026, null, null);

        assertThat(resposta.ano()).isEqualTo(2026);
        assertThat(resposta.semestre()).isNull();
        assertThat(resposta.quantidadeVendas()).isEqualTo(6);
        assertThat(resposta.valorTotal())
                .isEqualByComparingTo("630000.00");
        assertThat(resposta.ticketMedio())
                .isEqualByComparingTo("105000.00");

        assertThat(resposta.vendasPorMarca())
                .extracting("marca", "quantidade", "valorTotal")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                "Honda", 1L, new BigDecimal("90000.00")
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                "Toyota", 3L, new BigDecimal("330000.00")
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                "Volkswagen", 2L,
                                new BigDecimal("210000.00")
                        )
                );

        assertThat(resposta.vendasPorModelo())
                .extracting("marca", "modelo", "quantidade", "valorTotal")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                "Honda", "City", 1L,
                                new BigDecimal("90000.00")
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                "Toyota", "Corolla", 2L,
                                new BigDecimal("230000.00")
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                "Toyota", "Yaris", 1L,
                                new BigDecimal("100000.00")
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                "Volkswagen", "Polo", 2L,
                                new BigDecimal("210000.00")
                        )
                );

        assertThat(resposta.maisVendidos())
                .extracting("marca", "modelo", "quantidade")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                "Toyota", "Corolla", 2L
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                "Volkswagen", "Polo", 2L
                        )
                );

        assertThat(resposta.menosVendidos())
                .extracting("marca", "modelo", "quantidade")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                "Honda", "City", 1L
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                "Toyota", "Yaris", 1L
                        )
                );
    }

    @Test
    void deveConsultarPrimeiroSemestre() {
        when(vendaRepository.buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 7, 1),
                ""
        )).thenReturn(List.of());

        relatorioVendasService.gerar(2026, 1, null);

        verify(vendaRepository).buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 7, 1),
                ""
        );
    }

    @Test
    void deveConsultarSegundoSemestre() {
        when(vendaRepository.buscarParaRelatorio(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2027, 1, 1),
                ""
        )).thenReturn(List.of());

        relatorioVendasService.gerar(2026, 2, null);

        verify(vendaRepository).buscarParaRelatorio(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2027, 1, 1),
                ""
        );
    }

    @Test
    void deveNormalizarFiltroDeMarca() {
        when(vendaRepository.buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                "Toyota"
        )).thenReturn(List.of());

        relatorioVendasService.gerar(2026, null, "  Toyota  ");

        verify(vendaRepository).buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                "Toyota"
        );
    }

    @Test
    void deveRetornarValoresZeradosEListasVaziasSemVendas() {
        when(vendaRepository.buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                ""
        )).thenReturn(List.of());

        RelatorioVendasResponse resposta =
                relatorioVendasService.gerar(2026, null, "   ");

        assertThat(resposta.quantidadeVendas()).isZero();
        assertThat(resposta.valorTotal())
                .isEqualByComparingTo("0.00");
        assertThat(resposta.ticketMedio())
                .isEqualByComparingTo("0.00");
        assertThat(resposta.vendasPorMarca()).isEmpty();
        assertThat(resposta.vendasPorModelo()).isEmpty();
        assertThat(resposta.maisVendidos()).isEmpty();
        assertThat(resposta.menosVendidos()).isEmpty();
    }

    @Test
    void deveRejeitarAnoInvalido() {
        assertThatThrownBy(
                () -> relatorioVendasService.gerar(0, null, null)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O ano deve ser maior que zero.");
    }

    @Test
    void deveRejeitarSemestreInvalido() {
        assertThatThrownBy(
                () -> relatorioVendasService.gerar(2026, 3, null)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O semestre deve ser 1 ou 2.");
    }

    private Venda criarVenda(
            String marca,
            String modelo,
            String valor
    ) {
        Veiculo veiculo = new Veiculo();
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);

        Venda venda = new Venda();
        venda.setVeiculo(veiculo);
        venda.setValor(new BigDecimal(valor));
        return venda;
    }
}
