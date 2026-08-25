package com.concessionaria.backend.security;

import com.concessionaria.backend.model.Role;
import com.concessionaria.backend.model.User;
import com.concessionaria.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteVendaSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String tokenValido;

    @BeforeEach
    void criarUsuarioAutenticado() {
        String email =
                "marina." + UUID.randomUUID() + "@ufape.edu.br";

        User usuario = new User(
                null,
                "Marina Oliveira",
                email,
                passwordEncoder.encode("senhaTeste"),
                Role.USER
        );

        User usuarioSalvo =
                userRepository.saveAndFlush(usuario);

        tokenValido =
                jwtService.generateToken(usuarioSalvo);
    }

    @Test
    void deveBloquearConsultaDeClientesSemToken() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearDetalheDeClienteSemToken() throws Exception {
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearConsultaDeVendasSemToken() throws Exception {
        mockMvc.perform(get("/api/vendas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearDetalheDeVendaSemToken() throws Exception {
        mockMvc.perform(get("/api/vendas/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirConsultaDeClientesComTokenValido() throws Exception {
        mockMvc.perform(get("/api/clientes")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirConsultaDeVendasComTokenValido() throws Exception {
        mockMvc.perform(get("/api/vendas")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void deveBloquearClientesComTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/clientes")
                        .header(
                                "Authorization",
                                "Bearer token-invalido"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearVendasComTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/vendas")
                        .header(
                                "Authorization",
                                "Bearer token-invalido"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearAtualizacaoDeClienteSemToken() throws Exception {
        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nome": "Marina Oliveira",
                              "cpf": "98765432100",
                              "telefone": "81987654321",
                              "email": "marina.oliveira@teste.com"
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearHistoricoDeComprasSemToken() throws Exception {
        mockMvc.perform(get("/api/clientes/1/historico-compras"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirUsuarioAutenticadoAcessarAtualizacao()
            throws Exception {

        mockMvc.perform(put("/api/clientes/999999999")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nome": "Marina Oliveira",
                              "cpf": "98765432100",
                              "telefone": "81987654321",
                              "email": "marina.oliveira@teste.com"
                            }
                            """))
                .andExpect(status().isNotFound());
    }

    @Test
    void devePermitirUsuarioAutenticadoAcessarHistorico()
            throws Exception {

        mockMvc.perform(get("/api/clientes/999999999/historico-compras")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        ))
                .andExpect(status().isNotFound());
    }
}