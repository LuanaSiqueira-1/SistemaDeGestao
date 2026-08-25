package com.concessionaria.backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VeiculoUpdateDTO;
import com.concessionaria.backend.exception.GlobalExceptionHandler;
import com.concessionaria.backend.exception.VeiculoNaoEncontradoException;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.service.VeiculoService;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

    @Mock
    private VeiculoService veiculoService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {

        VeiculoController controller =
                new VeiculoController(veiculoService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveCadastrarVeiculoERetornar201() throws Exception {

        VeiculoResponse resposta = new VeiculoResponse(
                1L,
                "Toyota",
                "Corolla",
                2024,
                new BigDecimal("150000.00"),
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoService.cadastrar(any()))
                .thenReturn(resposta);

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "marca": "Toyota",
                                  "modelo": "Corolla",
                                  "ano": 2024,
                                  "cor": "Prata",
                                  "quilometragem": 0,
                                  "preco": 150000.00,
                                  "status": "DISPONIVEL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.status")
                        .value("DISPONIVEL"));
    }

    @Test
    void deveRetornar400QuandoCadastroForInvalido()
            throws Exception {

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "marca": "",
                                  "modelo": "",
                                  "ano": 2024,
                                  "quilometragem": -1,
                                  "preco": 0,
                                  "status": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro")
                        .value("Dados inválidos"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Um ou mais campos estão inválidos."))
                .andExpect(jsonPath("$.campos.marca")
                        .value("A marca é obrigatória"))
                .andExpect(jsonPath("$.campos.modelo")
                        .value("O modelo é obrigatório"))
                .andExpect(jsonPath("$.campos.quilometragem")
                        .value("A quilometragem não pode ser negativa"))
                .andExpect(jsonPath("$.campos.preco")
                        .value("O preço deve ser maior que zero"))
                .andExpect(jsonPath("$.campos.status")
                        .value("O status é obrigatório"));

        verify(veiculoService, never()).cadastrar(any());
    }

    @Test
    void deveListarVeiculosERetornar200()
            throws Exception {

        VeiculoListagemResponse veiculo =
                new VeiculoListagemResponse(
                        1L,
                        "Honda",
                        "Civic",
                        2024,
                        new BigDecimal("120000.00"),
                        StatusVeiculo.DISPONIVEL
                );

        when(veiculoService.listarVeiculos())
                .thenReturn(List.of(veiculo));

        mockMvc.perform(get("/api/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].marca")
                        .value("Honda"))
                .andExpect(jsonPath("$[0].modelo")
                        .value("Civic"));
    }

    @Test
    void deveRetornar200EListaVazia()
            throws Exception {

        when(veiculoService.listarVeiculos())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/veiculos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /*
     * US10 - edição válida de veículo
     */
    @Test
    void deveAtualizarVeiculoERetornar200()
            throws Exception {

        VeiculoResponse resposta = new VeiculoResponse(
                1L,
                "Toyota",
                "Corolla Cross",
                2025,
                new BigDecimal("180000.00"),
                StatusVeiculo.DISPONIVEL
        );

        when(veiculoService.atualizar(
                eq(1L),
                any(VeiculoUpdateDTO.class)
        )).thenReturn(resposta);

        mockMvc.perform(put("/api/veiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "marca": "Toyota",
                                  "modelo": "Corolla Cross",
                                  "ano": 2025,
                                  "cor": "Preto",
                                  "quilometragem": 500,
                                  "preco": 180000.00,
                                  "status": "DISPONIVEL"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.marca")
                        .value("Toyota"))
                .andExpect(jsonPath("$.modelo")
                        .value("Corolla Cross"))
                .andExpect(jsonPath("$.ano")
                        .value(2025))
                .andExpect(jsonPath("$.preco")
                        .value(180000.00))
                .andExpect(jsonPath("$.status")
                        .value("DISPONIVEL"));

        verify(veiculoService).atualizar(
                eq(1L),
                any(VeiculoUpdateDTO.class)
        );
    }

    /*
     * US10 - validação dos dados de edição
     */
    @Test
    void deveRetornar400QuandoAtualizacaoForInvalida()
            throws Exception {

        mockMvc.perform(put("/api/veiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "marca": "",
                                  "modelo": "",
                                  "ano": null,
                                  "cor": "Prata",
                                  "quilometragem": 1000,
                                  "preco": 0,
                                  "status": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.erro")
                        .value("Dados inválidos"))
                .andExpect(jsonPath("$.campos.marca")
                        .value("A marca não pode estar em branco"))
                .andExpect(jsonPath("$.campos.modelo")
                        .value("O modelo não pode estar em branco"))
                .andExpect(jsonPath("$.campos.ano")
                        .value("O ano não pode ser nulo"))
                .andExpect(jsonPath("$.campos.preco")
                        .value("O preço deve ser maior que zero"))
                .andExpect(jsonPath("$.campos.status")
                        .value("O status não pode ser nulo"));

        verify(
                veiculoService,
                never()
        ).atualizar(
                any(),
                any(VeiculoUpdateDTO.class)
        );
    }

    /*
     * US10 - veículo inexistente deve retornar 404
     */
    @Test
    void deveRetornar404QuandoVeiculoNaoExistir()
            throws Exception {

        when(veiculoService.atualizar(
                eq(99L),
                any(VeiculoUpdateDTO.class)
        )).thenThrow(
                new VeiculoNaoEncontradoException(99L)
        );

        mockMvc.perform(put("/api/veiculos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "marca": "Toyota",
                                  "modelo": "Corolla",
                                  "ano": 2024,
                                  "cor": "Prata",
                                  "quilometragem": 1000,
                                  "preco": 150000.00,
                                  "status": "DISPONIVEL"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Veículo não encontrado com o ID: 99"));
    }
}