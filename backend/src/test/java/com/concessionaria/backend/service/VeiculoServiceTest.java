package com.concessionaria.backend.service;

import com.concessionaria.backend.dto.VeiculoCadastroRequest;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    private VeiculoService veiculoService;

    @BeforeEach
    void configurar() {
        veiculoService = new VeiculoService(veiculoRepository);
    }

    @Test
    void deveCadastrarVeiculoValido() {
        VeiculoCadastroRequest request = new VeiculoCadastroRequest(
                "Toyota",
                "Corolla",
                2024,
                "Prata",
                0L,
                new BigDecimal("150000.00"),
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocacao -> {
            Veiculo veiculo = invocacao.getArgument(0);
            veiculo.setId(1L);
            return veiculo;
        });

        VeiculoResponse resposta = veiculoService.cadastrar(request);

        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoRepository).save(captor.capture());

        Veiculo veiculoSalvo = captor.getValue();
        assertThat(veiculoSalvo.getMarca()).isEqualTo("Toyota");
        assertThat(veiculoSalvo.getModelo()).isEqualTo("Corolla");
        assertThat(veiculoSalvo.getStatus()).isEqualTo(StatusVeiculo.DISPONIVEL);
        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.preco()).isEqualByComparingTo("150000.00");
    }

    @Test
    void deveListarVeiculosCadastrados() {
        Veiculo veiculo = criarVeiculo(1L, "Honda", "Civic");
        when(veiculoRepository.findAll()).thenReturn(List.of(veiculo));

        List<VeiculoListagemResponse> resposta = veiculoService.listarVeiculos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).id()).isEqualTo(1L);
        assertThat(resposta.get(0).marca()).isEqualTo("Honda");
        assertThat(resposta.get(0).modelo()).isEqualTo("Civic");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremVeiculos() {
        when(veiculoRepository.findAll()).thenReturn(List.of());

        List<VeiculoListagemResponse> resposta = veiculoService.listarVeiculos();

        assertThat(resposta).isEmpty();
    }

    private Veiculo criarVeiculo(Long id, String marca, String modelo) {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);
        veiculo.setAno(2024);
        veiculo.setPreco(new BigDecimal("120000.00"));
        veiculo.setStatus(StatusVeiculo.DISPONIVEL);
        return veiculo;
    }
}