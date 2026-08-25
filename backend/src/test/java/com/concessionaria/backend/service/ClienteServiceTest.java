package com.concessionaria.backend.service;

import com.concessionaria.backend.dto.ClienteAtualizacaoRequest;
import com.concessionaria.backend.dto.ClienteResponse;
import com.concessionaria.backend.exception.ClienteNaoEncontradoException;
import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteService clienteService;

    @BeforeEach
    void configurar() {
        clienteService = new ClienteService(clienteRepository);
    }

    @Test
    void deveAtualizarClienteExistente() {
        Cliente cliente = criarCliente();

        ClienteAtualizacaoRequest request = new ClienteAtualizacaoRequest(
                "Rianna Vaz",
                "12345678901",
                "87999999999",
                "rianna@teste.com"
        );

        when(clienteRepository.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        ClienteResponse resposta = clienteService.atualizar(1L, request);

        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Rianna Vaz");
        assertThat(resposta.cpf()).isEqualTo("12345678901");
        assertThat(resposta.telefone()).isEqualTo("87999999999");
        assertThat(resposta.email()).isEqualTo("rianna@teste.com");

        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExistir() {
        ClienteAtualizacaoRequest request = new ClienteAtualizacaoRequest(
                "Rianna Vaz",
                "12345678901",
                "87999999999",
                "rianna@teste.com"
        );

        when(clienteRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(99L, request))
                .isInstanceOf(ClienteNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado com o ID: 99");

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    private Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Nome Antigo");
        cliente.setCpf("00000000000");
        cliente.setTelefone("87000000000");
        cliente.setEmail("antigo@teste.com");
        return cliente;
    }
}