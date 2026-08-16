package com.concessionaria.backend.service;

import com.concessionaria.backend.dto.VendaDetalheResponse;
import com.concessionaria.backend.dto.VendaListagemResponse;
import com.concessionaria.backend.dto.VendaRequestDTO;
import com.concessionaria.backend.dto.VendaResponseDTO;
import com.concessionaria.backend.exception.VendaNaoEncontradaException;
import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.model.Venda;
import com.concessionaria.backend.repository.ClienteRepository;
import com.concessionaria.backend.repository.VeiculoRepository;
import com.concessionaria.backend.repository.VendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VendaService vendaService;

    @Test
    void deveRegistrarVenda() {
        Cliente cliente = criarCliente();
        Veiculo veiculo = criarVeiculo();

        VendaRequestDTO request = new VendaRequestDTO(
                LocalDate.of(2026, 8, 16),
                new BigDecimal("85000.00"),
                veiculo.getId(),
                cliente.getId()
        );

        when(clienteRepository.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(veiculoRepository.findById(2L))
                .thenReturn(Optional.of(veiculo));

        when(vendaRepository.save(any(Venda.class)))
                .thenAnswer(invocation -> {
                    Venda venda = invocation.getArgument(0);
                    venda.setId(10L);
                    return venda;
                });

        VendaResponseDTO resposta = vendaService.registrarVenda(request);

        assertThat(resposta.id()).isEqualTo(10L);
        assertThat(resposta.dataVenda())
                .isEqualTo(LocalDate.of(2026, 8, 16));
        assertThat(resposta.valor())
                .isEqualByComparingTo("85000.00");

        verify(vendaRepository).save(any(Venda.class));
    }

    @Test
    void devePesquisarVendasComPaginacao() {
        Venda venda = criarVenda();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Venda> pagina = new PageImpl<>(
                List.of(venda),
                pageable,
                1
        );

        when(vendaRepository.pesquisar(
                "Rianna",
                "Toyota",
                pageable
        )).thenReturn(pagina);

        Page<VendaListagemResponse> resultado =
                vendaService.listar("Rianna", "Toyota", pageable);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).hasSize(1);

        VendaListagemResponse item = resultado.getContent().get(0);

        assertThat(item.id()).isEqualTo(10L);
        assertThat(item.cliente().nome()).isEqualTo("Rianna");
        assertThat(item.veiculo().marca()).isEqualTo("Toyota");
    }

    @Test
    void deveBuscarVendaPorId() {
        Venda venda = criarVenda();

        when(vendaRepository.findById(10L))
                .thenReturn(Optional.of(venda));

        VendaDetalheResponse resposta =
                vendaService.buscarPorId(10L);

        assertThat(resposta.id()).isEqualTo(10L);
        assertThat(resposta.cliente().nome()).isEqualTo("Rianna");
        assertThat(resposta.veiculo().modelo()).isEqualTo("Corolla");
        assertThat(resposta.valor())
                .isEqualByComparingTo("85000.00");
    }

    @Test
    void deveLancarExcecaoQuandoVendaNaoExistir() {
        when(vendaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.buscarPorId(99L))
                .isInstanceOf(VendaNaoEncontradaException.class)
                .hasMessage("Venda não encontrada com o ID: 99");
    }

    private Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Rianna");
        cliente.setCpf("12345678900");
        cliente.setTelefone("87999999999");
        cliente.setEmail("rianna@email.com");
        return cliente;
    }

    private Veiculo criarVeiculo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(2L);
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2024);
        veiculo.setPreco(new BigDecimal("90000.00"));
        veiculo.setStatus(StatusVeiculo.VENDIDO);
        return veiculo;
    }

    private Venda criarVenda() {
        return new Venda(
                10L,
                LocalDate.of(2026, 8, 16),
                new BigDecimal("85000.00"),
                criarVeiculo(),
                criarCliente()
        );
    }
}