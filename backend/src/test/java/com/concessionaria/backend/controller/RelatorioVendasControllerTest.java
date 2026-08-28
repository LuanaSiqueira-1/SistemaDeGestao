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

import com.concessionaria.backend.dto.RelatorioVendasResponse;
import com.concessionaria.backend.service.RelatorioVendasService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RelatorioVendasControllerTest {

    @Mock
    private RelatorioVendasService relatorioVendasService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        RelatorioVendasController controller =
                new RelatorioVendasController(relatorioVendasService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void deveGerarRelatorioERetornar200() throws Exception {
        RelatorioVendasResponse resposta = new RelatorioVendasResponse(
                2026,
                1,
                2,
                new BigDecimal("200000.00"),
                new BigDecimal("100000.00"),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        when(relatorioVendasService.gerar(2026, 1, "Toyota"))
                .thenReturn(resposta);

        mockMvc.perform(get("/api/relatorios/vendas")
                        .param("ano", "2026")
                        .param("semestre", "1")
                        .param("marca", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ano").value(2026))
                .andExpect(jsonPath("$.semestre").value(1))
                .andExpect(jsonPath("$.quantidadeVendas").value(2))
                .andExpect(jsonPath("$.valorTotal").value(200000.00))
                .andExpect(jsonPath("$.ticketMedio").value(100000.00));

        verify(relatorioVendasService).gerar(2026, 1, "Toyota");
    }

    @Test
    void deveRetornar400QuandoAnoForAusente() throws Exception {
        mockMvc.perform(get("/api/relatorios/vendas"))
                .andExpect(status().isBadRequest());

        verify(relatorioVendasService, never())
                .gerar(0, null, null);
    }

    @Test
    void deveRetornar400QuandoAnoNaoForInteiro() throws Exception {
        mockMvc.perform(get("/api/relatorios/vendas")
                        .param("ano", "invalido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoAnoForMenorOuIgualAZero()
            throws Exception {
        mockMvc.perform(get("/api/relatorios/vendas")
                        .param("ano", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoSemestreForInvalido() throws Exception {
        mockMvc.perform(get("/api/relatorios/vendas")
                        .param("ano", "2026")
                        .param("semestre", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoSemestreNaoForInteiro()
            throws Exception {
        mockMvc.perform(get("/api/relatorios/vendas")
                        .param("ano", "2026")
                        .param("semestre", "invalido"))
                .andExpect(status().isBadRequest());
    }
}
