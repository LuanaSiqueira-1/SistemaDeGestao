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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                "rianna." + UUID.randomUUID() + "@ufape.edu.br";

        User usuario = new User(
                null,
                "Rianna",
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
}