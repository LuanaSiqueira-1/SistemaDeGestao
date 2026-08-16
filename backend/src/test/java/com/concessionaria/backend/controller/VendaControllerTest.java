package com.concessionaria.backend.controller;

import com.concessionaria.backend.dto.ClienteDetalheResponse;
import com.concessionaria.backend.dto.ClienteListagemResponse;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VendaDetalheResponse;
import com.concessionaria.backend.dto.VendaListagemResponse;
import com.concessionaria.backend.dto.VendaResponseDTO;
import com.concessionaria.backend.exception.GlobalExceptionHandler;
import com.concessionaria.backend.exception.VendaNaoEncontradaException;
import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.model.Venda;
import com.concessionaria.backend.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VendaControllerTest {

    @Mock
    private VendaService vendaService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        VendaController controller = new VendaController(vendaService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    void deveRegistrarVendaERetornar201() throws Exception {
        Venda venda = criarVenda();

        when(vendaService.registrarVenda(any()))
                .thenReturn(new VendaResponseDTO(venda));

        mockMvc.perform(post("/api/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dataVenda": "2026-08-16",
                                  "valor": 85000.00,
                                  "veiculoId": 2,
                                  "clienteId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.valor").value(85000.00));

        verify(vendaService).registrarVenda(any());
    }

    @Test
    void deveRetornar400QuandoRegistroForInvalido() throws Exception {
        mockMvc.perform(post("/api/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dataVenda": null,
                                  "valor": 0,
                                  "veiculoId": null,
                                  "clienteId": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Dados inválidos"))
                .andExpect(jsonPath("$.campos.dataVenda")
                        .value("A data da venda é obrigatória."))
                .andExpect(jsonPath("$.campos.valor")
                        .value("O valor da venda deve ser maior que zero."))
                .andExpect(jsonPath("$.campos.veiculoId")
                        .value("O ID do veículo é obrigatório."))
                .andExpect(jsonPath("$.campos.clienteId")
                        .value("O ID do cliente é obrigatório."));

        verify(vendaService, never()).registrarVenda(any());
    }

    @Test
    void devePesquisarVendasComPaginacaoERetornar200() throws Exception {
        VendaListagemResponse venda = criarVendaListagemResponse();
        Page<VendaListagemResponse> pagina =
                new PageImpl<>(
                        List.of(venda),
                        PageRequest.of(0, 10),
                        1
                );

        when(vendaService.listar(
                eq("Rianna"),
                eq("Toyota"),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(get("/api/vendas")
                        .param("cliente", "Rianna")
                        .param("veiculo", "Toyota")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].cliente.nome")
                        .value("Rianna"))
                .andExpect(jsonPath("$.content[0].veiculo.marca")
                        .value("Toyota"));

        verify(vendaService).listar(
                eq("Rianna"),
                eq("Toyota"),
                any(Pageable.class)
        );
    }

    @Test
    void deveBuscarVendaPorIdERetornar200() throws Exception {
        when(vendaService.buscarPorId(10L))
                .thenReturn(criarVendaDetalheResponse());

        mockMvc.perform(get("/api/vendas/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.cliente.nome").value("Rianna"))
                .andExpect(jsonPath("$.veiculo.modelo").value("Corolla"));
    }

    @Test
    void deveRetornar404QuandoVendaNaoExistir() throws Exception {
        when(vendaService.buscarPorId(99L))
                .thenThrow(new VendaNaoEncontradaException(99L));

        mockMvc.perform(get("/api/vendas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Venda não encontrada com o ID: 99"));
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

    private VendaListagemResponse criarVendaListagemResponse() {
        ClienteListagemResponse cliente =
                new ClienteListagemResponse(
                        1L,
                        "Rianna",
                        "12345678900"
                );

        VeiculoListagemResponse veiculo =
                new VeiculoListagemResponse(
                        2L,
                        "Toyota",
                        "Corolla",
                        2024,
                        new BigDecimal("90000.00"),
                        StatusVeiculo.VENDIDO
                );

        return new VendaListagemResponse(
                10L,
                LocalDate.of(2026, 8, 16),
                new BigDecimal("85000.00"),
                cliente,
                veiculo
        );
    }

    private VendaDetalheResponse criarVendaDetalheResponse() {
        ClienteDetalheResponse cliente =
                new ClienteDetalheResponse(
                        1L,
                        "Rianna",
                        "12345678900",
                        "87999999999",
                        "rianna@email.com"
                );

        VeiculoResponse veiculo =
                new VeiculoResponse(
                        2L,
                        "Toyota",
                        "Corolla",
                        2024,
                        new BigDecimal("90000.00"),
                        StatusVeiculo.VENDIDO
                );

        return new VendaDetalheResponse(
                10L,
                LocalDate.of(2026, 8, 16),
                new BigDecimal("85000.00"),
                cliente,
                veiculo
        );
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
}