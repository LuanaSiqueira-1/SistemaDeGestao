package com.concessionaria.backend.service;

import com.concessionaria.backend.dto.VeiculoCadastroRequest;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VeiculoUpdateDTO;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    @Test
    void deveAtualizarVeiculoExistente() {
        Veiculo veiculo = criarVeiculo(1L, "Honda", "Civic");
        VeiculoUpdateDTO dto = criarAtualizacao(StatusVeiculo.EM_MANUTENCAO);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        VeiculoResponse resposta = veiculoService.atualizar(1L, dto);

        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.marca()).isEqualTo("Toyota");
        assertThat(resposta.modelo()).isEqualTo("Corolla");
        assertThat(resposta.status()).isEqualTo(StatusVeiculo.EM_MANUTENCAO);
        assertThat(veiculo.getCor()).isEqualTo("Prata");
        assertThat(veiculo.getQuilometragem()).isEqualTo(1000L);

        verify(veiculoRepository).save(veiculo);
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoExistir() {
        VeiculoUpdateDTO dto = criarAtualizacao(StatusVeiculo.DISPONIVEL);

        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.atualizar(99L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Veículo não encontrado");

        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    void naoDevePermitirQueVeiculoVendidoVolteParaDisponivel() {
        Veiculo veiculo = criarVeiculo(1L, "Honda", "Civic");
        veiculo.setStatus(StatusVeiculo.VENDIDO);

        VeiculoUpdateDTO dto = criarAtualizacao(StatusVeiculo.DISPONIVEL);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> veiculoService.atualizar(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não é permitido alterar");

        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    void devePermitirAtualizacaoMantendoStatusVendido() {
        Veiculo veiculo = criarVeiculo(1L, "Honda", "Civic");
        veiculo.setStatus(StatusVeiculo.VENDIDO);

        VeiculoUpdateDTO dto = criarAtualizacao(StatusVeiculo.VENDIDO);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        VeiculoResponse resposta = veiculoService.atualizar(1L, dto);

        assertThat(resposta.status()).isEqualTo(StatusVeiculo.VENDIDO);
        verify(veiculoRepository).save(veiculo);
    }

    private VeiculoUpdateDTO criarAtualizacao(StatusVeiculo status) {
        VeiculoUpdateDTO dto = new VeiculoUpdateDTO();
        dto.setMarca("Toyota");
        dto.setModelo("Corolla");
        dto.setAno(2025);
        dto.setCor("Prata");
        dto.setQuilometragem(1000L);
        dto.setPreco(new BigDecimal("160000.00"));
        dto.setStatus(status);
        return dto;
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