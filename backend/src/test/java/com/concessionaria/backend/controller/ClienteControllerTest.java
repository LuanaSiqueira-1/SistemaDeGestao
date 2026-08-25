package com.concessionaria.backend.controller;

import com.concessionaria.backend.dto.ClienteAtualizacaoRequest;
import com.concessionaria.backend.dto.ClienteResponse;
import com.concessionaria.backend.exception.ClienteNaoEncontradoException;
import com.concessionaria.backend.exception.GlobalExceptionHandler;
import com.concessionaria.backend.service.ClienteService;
import com.concessionaria.backend.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private VendaService vendaService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        ClienteController controller =
                new ClienteController(clienteService, vendaService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveAtualizarClienteERetornar200() throws Exception {
        ClienteResponse resposta = new ClienteResponse(
                1L,
                "Marina Oliveira",
                "12345678901",
                "87999999999",
                "marina.oliveira@teste.com"
        );

        when(clienteService.atualizar(
                eq(1L),
                any(ClienteAtualizacaoRequest.class)
        )).thenReturn(resposta);

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Marina Oliveira",
                                  "cpf": "12345678901",
                                  "telefone": "87999999999",
                                  "email": "marina.oliveira@teste.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Marina Oliveira"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.telefone").value("87999999999"))
                .andExpect(jsonPath("$.email").value("marina.oliveira@teste.com"));

        verify(clienteService).atualizar(
                eq(1L),
                any(ClienteAtualizacaoRequest.class)
        );
    }

    @Test
    void deveRetornar400QuandoAtualizacaoForInvalida() throws Exception {
        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "cpf": "",
                                  "telefone": "",
                                  "email": "email-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Dados inválidos"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.cpf").exists())
                .andExpect(jsonPath("$.campos.telefone").exists())
                .andExpect(jsonPath("$.campos.email").exists());
    }

    @Test
    void deveRetornar404QuandoClienteNaoExistir() throws Exception {
        when(clienteService.atualizar(
                eq(99L),
                any(ClienteAtualizacaoRequest.class)
        )).thenThrow(new ClienteNaoEncontradoException(99L));

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Marina Oliveira",
                                  "cpf": "12345678901",
                                  "telefone": "87999999999",
                                  "email": "marina.oliveira@teste.com"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro")
                        .value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Cliente não encontrado com o ID: 99"));
    }
}