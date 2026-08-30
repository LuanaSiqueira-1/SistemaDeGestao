package com.concessionaria.backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.concessionaria.backend.dto.EstoqueAgrupamentoResponse;
import com.concessionaria.backend.dto.EstoqueResumoResponse;
import com.concessionaria.backend.exception.GlobalExceptionHandler;
import com.concessionaria.backend.exception.StatusEstoqueInvalidoException;
import com.concessionaria.backend.service.EstoqueService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EstoqueControllerTest {

    private static final String MARCA_TOYOTA = "Toyota";
    private static final String STATUS_DISPONIVEL = "DISPONIVEL";
    private static final String STATUS_INVALIDO = "INVALIDO";

    @Mock
    private EstoqueService estoqueService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        EstoqueController controller =
                new EstoqueController(estoqueService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornarResumoDoEstoqueComFiltros() throws Exception {
        EstoqueResumoResponse resposta = new EstoqueResumoResponse(
                3L,
                2L,
                1L,
                new BigDecimal("66.67"),
                new BigDecimal("150000.00"),
                List.of(
                        new EstoqueAgrupamentoResponse(
                                MARCA_TOYOTA,
                                3L,
                                2L
                        )
                ),
                List.of(),
                List.of()
        );

        when(estoqueService.resumir(
                MARCA_TOYOTA,
                STATUS_DISPONIVEL
        )).thenReturn(resposta);

        mockMvc.perform(get("/api/estoque/resumo")
                        .param("marca", MARCA_TOYOTA)
                        .param("status", STATUS_DISPONIVEL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeTotal").value(3))
                .andExpect(jsonPath("$.quantidadeDisponivel").value(2))
                .andExpect(jsonPath("$.quantidadeIndisponivel").value(1))
                .andExpect(jsonPath("$.percentualDisponivel")
                        .value(66.67))
                .andExpect(jsonPath("$.valorTotalDisponivel")
                        .value(150000.00))
                .andExpect(jsonPath("$.porMarca[0].nome")
                        .value(MARCA_TOYOTA));

        verify(estoqueService).resumir(
                MARCA_TOYOTA,
                STATUS_DISPONIVEL
        );
    }

    @Test
    void deveRetornar400QuandoStatusForInvalido() throws Exception {
        when(estoqueService.resumir(null, STATUS_INVALIDO))
                .thenThrow(
                        new StatusEstoqueInvalidoException(
                                STATUS_INVALIDO
                        )
                );

        mockMvc.perform(get("/api/estoque/resumo")
                        .param("status", STATUS_INVALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro")
                        .value("Dados inválidos"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Status de veículo inválido: "
                                        + STATUS_INVALIDO
                        ));

        verify(estoqueService).resumir(null, STATUS_INVALIDO);
    }
}