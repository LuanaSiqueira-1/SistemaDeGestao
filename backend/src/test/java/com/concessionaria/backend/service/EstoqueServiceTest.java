package com.concessionaria.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.concessionaria.backend.dto.EstoqueResumoResponse;
import com.concessionaria.backend.exception.StatusEstoqueInvalidoException;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.repository.VeiculoRepository;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    private EstoqueService estoqueService;

    @BeforeEach
    void configurar() {
        estoqueService = new EstoqueService(veiculoRepository);
    }

    @Test
    void deveCalcularResumoCompletoDoEstoque() {
        Veiculo disponivel1 = veiculo(
                1L,
                "Toyota",
                "Corolla",
                new BigDecimal("50000.00"),
                StatusVeiculo.DISPONIVEL
        );

        Veiculo disponivel2 = veiculo(
                2L,
                "Toyota",
                "Yaris",
                new BigDecimal("100000.00"),
                StatusVeiculo.DISPONIVEL
        );

        Veiculo vendido = veiculo(
                3L,
                "Honda",
                "Civic",
                new BigDecimal("150000.00"),
                StatusVeiculo.VENDIDO
        );

        Veiculo manutencao = veiculo(
                4L,
                "Honda",
                "HRV",
                new BigDecimal("200000.00"),
                StatusVeiculo.EM_MANUTENCAO
        );

        when(veiculoRepository.findAll())
                .thenReturn(List.of(
                        disponivel1,
                        disponivel2,
                        vendido,
                        manutencao
                ));

        EstoqueResumoResponse resposta =
                estoqueService.resumir(null, null);

        assertThat(resposta.quantidadeTotal()).isEqualTo(4);
        assertThat(resposta.quantidadeDisponivel()).isEqualTo(2);
        assertThat(resposta.quantidadeIndisponivel()).isEqualTo(2);
        assertThat(resposta.percentualDisponivel())
                .isEqualByComparingTo("50.00");
        assertThat(resposta.valorTotalDisponivel())
                .isEqualByComparingTo("150000.00");

        assertThat(resposta.porMarca()).hasSize(2);
        assertThat(resposta.porModelo()).hasSize(4);
        assertThat(resposta.porFaixaPreco()).hasSize(4);
    }

    @Test
    void deveRetornarZeroQuandoNaoHouverVeiculos() {
        when(veiculoRepository.findAll())
                .thenReturn(List.of());

        EstoqueResumoResponse resposta =
                estoqueService.resumir(null, null);

        assertThat(resposta.quantidadeTotal()).isZero();
        assertThat(resposta.quantidadeDisponivel()).isZero();
        assertThat(resposta.quantidadeIndisponivel()).isZero();
        assertThat(resposta.percentualDisponivel())
                .isEqualByComparingTo("0.00");
        assertThat(resposta.valorTotalDisponivel())
                .isEqualByComparingTo("0.00");
    }

    @Test
    void deveFiltrarPorMarcaIgnorandoMaiusculas() {
        Veiculo toyota = veiculo(
                1L,
                "Toyota",
                "Corolla",
                new BigDecimal("50000.00"),
                StatusVeiculo.DISPONIVEL
        );

        Veiculo honda = veiculo(
                2L,
                "Honda",
                "Civic",
                new BigDecimal("70000.00"),
                StatusVeiculo.VENDIDO
        );

        when(veiculoRepository.findAll())
                .thenReturn(List.of(toyota, honda));

        EstoqueResumoResponse resposta =
                estoqueService.resumir("toyota", null);

        assertThat(resposta.quantidadeTotal()).isEqualTo(1);
        assertThat(resposta.quantidadeDisponivel()).isEqualTo(1);
    }

    @Test
    void deveFiltrarPorStatus() {
        Veiculo disponivel = veiculo(
                1L,
                "Toyota",
                "Corolla",
                new BigDecimal("50000.00"),
                StatusVeiculo.DISPONIVEL
        );

        Veiculo vendido = veiculo(
                2L,
                "Honda",
                "Civic",
                new BigDecimal("70000.00"),
                StatusVeiculo.VENDIDO
        );

        when(veiculoRepository.findAll())
                .thenReturn(List.of(disponivel, vendido));

        EstoqueResumoResponse resposta =
                estoqueService.resumir(null, "vendido");

        assertThat(resposta.quantidadeTotal()).isEqualTo(1);
        assertThat(resposta.quantidadeDisponivel()).isZero();
        assertThat(resposta.quantidadeIndisponivel()).isEqualTo(1);
    }

    @Test
    void deveAceitarMarcaEmBranco() {
        Veiculo veiculo = veiculo(
                1L,
                "Toyota",
                "Corolla",
                new BigDecimal("50000.00"),
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoRepository.findAll())
                .thenReturn(List.of(veiculo));

        EstoqueResumoResponse resposta =
                estoqueService.resumir("   ", null);

        assertThat(resposta.quantidadeTotal()).isEqualTo(1);
    }

    @Test
    void deveAceitarStatusComEspacosEMaiusculas() {
        Veiculo veiculo = veiculo(
                1L,
                "Toyota",
                "Corolla",
                new BigDecimal("50000.00"),
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoRepository.findAll())
                .thenReturn(List.of(veiculo));

        EstoqueResumoResponse resposta =
                estoqueService.resumir(null, "  disponivel  ");

        assertThat(resposta.quantidadeTotal()).isEqualTo(1);
        assertThat(resposta.quantidadeDisponivel()).isEqualTo(1);
    }

    @Test
    void deveLancarExcecaoParaStatusInvalido() {
        assertThatThrownBy(
                () -> estoqueService.resumir(null, "INVALIDO")
        )
                .isInstanceOf(StatusEstoqueInvalidoException.class)
                .hasMessage("Status de veículo inválido: INVALIDO");
    }

    private Veiculo veiculo(
            Long id,
            String marca,
            String modelo,
            BigDecimal preco,
            StatusVeiculo status
    ) {
        Veiculo veiculo = new Veiculo();

        veiculo.setId(id);
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);
        veiculo.setPreco(preco);
        veiculo.setStatus(status);

        return veiculo;
    }
}
