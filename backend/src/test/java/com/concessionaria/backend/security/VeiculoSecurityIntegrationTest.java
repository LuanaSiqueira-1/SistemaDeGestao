package com.concessionaria.backend.security;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.concessionaria.backend.model.Role;
import com.concessionaria.backend.model.User;
import com.concessionaria.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VeiculoSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void deveBloquearConsultaSemToken() throws Exception {

        mockMvc.perform(get("/api/veiculos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearCadastroSemToken() throws Exception {

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVeiculoValido()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirConsultaComTokenValido() throws Exception {

        mockMvc.perform(get("/api/veiculos")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirCadastroComTokenValido() throws Exception {

        mockMvc.perform(post("/api/veiculos")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVeiculoValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));
    }

    /*
     * Q5-07 - atualização de veículo também exige autenticação
     */
    @Test
    void deveBloquearAtualizacaoSemToken() throws Exception {

        mockMvc.perform(put("/api/veiculos/1")
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
                .andExpect(status().isUnauthorized());
    }

    /*
     * Q5-07 - usuário autenticado pode atualizar veículo
     */
    @Test
    void devePermitirAtualizacaoComTokenValido() throws Exception {

        String respostaCadastro = mockMvc.perform(
                post("/api/veiculos")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVeiculoValido())
        )
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

        long veiculoId = objectMapper
                .readTree(respostaCadastro)
                .get("id")
                .asLong();

        mockMvc.perform(
                put("/api/veiculos/" + veiculoId)
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        )
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
                .andExpect(jsonPath("$.modelo")
                        .value("Corolla Cross"))
                .andExpect(jsonPath("$.ano")
                        .value(2025))
                .andExpect(jsonPath("$.status")
                        .value("DISPONIVEL"));
    }

    @Test
    void deveBloquearRequisicaoComTokenInvalido() throws Exception {

        mockMvc.perform(get("/api/veiculos")
                        .header(
                                "Authorization",
                                "Bearer token-invalido"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirConsultaComTokenDeAdmin() throws Exception {

        String emailAdmin =
                "admin." + UUID.randomUUID() + "@ufape.edu.br";

        User administrador = new User(
                null,
                "Administrador",
                emailAdmin,
                passwordEncoder.encode("senhaAdmin"),
                Role.ADMIN
        );

        User adminSalvo =
                userRepository.saveAndFlush(administrador);

        String tokenAdmin =
                jwtService.generateToken(adminSalvo);

        mockMvc.perform(get("/api/veiculos")
                        .header(
                                "Authorization",
                                "Bearer " + tokenAdmin
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void deveBloquearUsuarioComRoleIncorreta() throws Exception {

        mockMvc.perform(get("/api/admin/dashboard")
                        .header(
                                "Authorization",
                                "Bearer " + tokenValido
                        ))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAdminNaAreaAdministrativa() throws Exception {

        String emailAdmin =
                "admin.dashboard."
                        + UUID.randomUUID()
                        + "@ufape.edu.br";

        User administrador = new User(
                null,
                "Administrador",
                emailAdmin,
                passwordEncoder.encode("senhaAdmin"),
                Role.ADMIN
        );

        User adminSalvo =
                userRepository.saveAndFlush(administrador);

        String tokenAdmin =
                jwtService.generateToken(adminSalvo);

        mockMvc.perform(get("/api/admin/dashboard")
                        .header(
                                "Authorization",
                                "Bearer " + tokenAdmin
                        ))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Acesso autorizado! Você está na área administrativa."
                ));
    }

    private String jsonVeiculoValido() {

        return """
                {
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2024,
                  "cor": "Prata",
                  "quilometragem": 0,
                  "preco": 150000.00,
                  "status": "DISPONIVEL"
                }
                """;
    }
}