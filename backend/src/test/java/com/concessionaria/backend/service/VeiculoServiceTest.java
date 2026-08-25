package com.concessionaria.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.concessionaria.backend.dto.VeiculoCadastroRequest;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VeiculoUpdateDTO;
import com.concessionaria.backend.exception.VeiculoNaoEncontradoException;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.repository.VeiculoRepository;

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

        when(veiculoRepository.save(any(Veiculo.class)))
                .thenAnswer(invocacao -> {
                    Veiculo veiculo = invocacao.getArgument(0);
                    veiculo.setId(1L);
                    return veiculo;
                });

        VeiculoResponse resposta = veiculoService.cadastrar(request);

        ArgumentCaptor<Veiculo> captor =
                ArgumentCaptor.forClass(Veiculo.class);

        verify(veiculoRepository).save(captor.capture());

        Veiculo veiculoSalvo = captor.getValue();

        assertThat(veiculoSalvo.getMarca()).isEqualTo("Toyota");
        assertThat(veiculoSalvo.getModelo()).isEqualTo("Corolla");
        assertThat(veiculoSalvo.getStatus())
                .isEqualTo(StatusVeiculo.DISPONIVEL);

        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.preco())
                .isEqualByComparingTo("150000.00");
    }

    @Test
    void deveListarVeiculosCadastrados() {

        Veiculo veiculo = criarVeiculo(
                1L,
                "Honda",
                "Civic"
        );

        when(veiculoRepository.findAll())
                .thenReturn(List.of(veiculo));

        List<VeiculoListagemResponse> resposta =
                veiculoService.listarVeiculos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).id()).isEqualTo(1L);
        assertThat(resposta.get(0).marca()).isEqualTo("Honda");
        assertThat(resposta.get(0).modelo()).isEqualTo("Civic");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremVeiculos() {

        when(veiculoRepository.findAll())
                .thenReturn(List.of());

        List<VeiculoListagemResponse> resposta =
                veiculoService.listarVeiculos();

        assertThat(resposta).isEmpty();
    }

    /*
     * US10 - deve atualizar um veículo existente
     */
    @Test
    void deveAtualizarVeiculoExistente() {

        Veiculo veiculoExistente =
                criarVeiculo(1L, "Honda", "Civic");

        VeiculoUpdateDTO dto = criarUpdateDTO(
                "Toyota",
                "Corolla",
                2025,
                "Preto",
                1000L,
                "160000.00",
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoRepository.findById(1L))
                .thenReturn(Optional.of(veiculoExistente));

        when(veiculoRepository.save(any(Veiculo.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        VeiculoResponse resposta =
                veiculoService.atualizar(1L, dto);

        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.marca()).isEqualTo("Toyota");
        assertThat(resposta.modelo()).isEqualTo("Corolla");
        assertThat(resposta.ano()).isEqualTo(2025);
        assertThat(resposta.preco())
                .isEqualByComparingTo("160000.00");
        assertThat(resposta.status())
                .isEqualTo(StatusVeiculo.DISPONIVEL);

        verify(veiculoRepository).findById(1L);
        verify(veiculoRepository).save(veiculoExistente);
    }

    /*
     * US10 - deve retornar erro quando o veículo não existir
     */
    @Test
    void deveLancarExcecaoQuandoVeiculoNaoExistir() {

        VeiculoUpdateDTO dto = criarUpdateDTO(
                "Toyota",
                "Corolla",
                2025,
                "Preto",
                1000L,
                "160000.00",
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> veiculoService.atualizar(99L, dto)
        )
                .isInstanceOf(VeiculoNaoEncontradoException.class)
                .hasMessage("Veículo não encontrado com o ID: 99");

        verify(veiculoRepository, never())
                .save(any(Veiculo.class));
    }

    /*
     * Q5-07 - veículo vendido não pode voltar manualmente
     * para DISPONIVEL
     */
    @Test
    void deveImpedirAlteracaoDeVeiculoVendidoParaDisponivel() {

        Veiculo veiculoVendido =
                criarVeiculo(1L, "Toyota", "Corolla");

        veiculoVendido.setStatus(StatusVeiculo.VENDIDO);

        VeiculoUpdateDTO dto = criarUpdateDTO(
                "Toyota",
                "Corolla",
                2024,
                "Prata",
                10000L,
                "150000.00",
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoRepository.findById(1L))
                .thenReturn(Optional.of(veiculoVendido));

        assertThatThrownBy(
                () -> veiculoService.atualizar(1L, dto)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage(
                        "Regra de Negócio: Não é permitido alterar manualmente o status de um veículo já VENDIDO."
                );

        verify(veiculoRepository, never())
                .save(any(Veiculo.class));
    }

    private Veiculo criarVeiculo(
            Long id,
            String marca,
            String modelo
    ) {

        Veiculo veiculo = new Veiculo();

        veiculo.setId(id);
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);
        veiculo.setAno(2024);
        veiculo.setCor("Prata");
        veiculo.setQuilometragem(0L);
        veiculo.setPreco(new BigDecimal("120000.00"));
        veiculo.setStatus(StatusVeiculo.DISPONIVEL);

        return veiculo;
    }

    private VeiculoUpdateDTO criarUpdateDTO(
            String marca,
            String modelo,
            Integer ano,
            String cor,
            Long quilometragem,
            String preco,
            StatusVeiculo status
    ) {

        VeiculoUpdateDTO dto = new VeiculoUpdateDTO();

        dto.setMarca(marca);
        dto.setModelo(modelo);
        dto.setAno(ano);
        dto.setCor(cor);
        dto.setQuilometragem(quilometragem);
        dto.setPreco(new BigDecimal(preco));
        dto.setStatus(status);

        return dto;
    }
}