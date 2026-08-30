package com.concessionaria.backend.security;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.concessionaria.backend.model.Role;
import com.concessionaria.backend.model.User;
import com.concessionaria.backend.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IndicadoresSecurityIntegrationTest {

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

        User usuarioSalvo = userRepository.saveAndFlush(usuario);
        tokenValido = jwtService.generateToken(usuarioSalvo);
    }

    @Test
    void deveBloquearRelatorioDeVendasSemToken() throws Exception {
        mockMvc.perform(get("/api/relatorios/vendas")
                        .param("ano", "2026"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirRelatorioDeVendasComTokenValido()
            throws Exception {
        mockMvc.perform(get("/api/relatorios/vendas")
                        .param("ano", "2026")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void deveBloquearResumoDoEstoqueSemToken() throws Exception {
        mockMvc.perform(get("/api/estoque/resumo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirResumoDoEstoqueComTokenValido()
            throws Exception {
        mockMvc.perform(get("/api/estoque/resumo")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        ))
                .andExpect(status().isOk());
    }
}