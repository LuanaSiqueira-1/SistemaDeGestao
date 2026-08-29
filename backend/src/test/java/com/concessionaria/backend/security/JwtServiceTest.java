package com.concessionaria.backend.security;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

    private static final String SECRET_KEY =
            "c2VjcmV0LWtleS1mb3ItdGVzdC1qd3Qtc2lnbmF0dXJlLWtleQ==";

    private JwtService jwtService;

    private UserDetails usuario;

    @BeforeEach
    void configurar() {
        jwtService = new JwtService(SECRET_KEY);

        usuario = User
                .withUsername("marina.oliveira@teste.com")
                .password("senha-ficticia")
                .roles("USER")
                .build();
    }

    @Test
    void deveGerarTokenValidoParaUsuario() {
        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token))
                .isEqualTo(usuario.getUsername());
        assertThat(jwtService.isTokenValid(token, usuario))
                .isTrue();
    }

    @Test
    void deveGerarTokenComClaimsAdicionais() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtService.generateToken(claims, usuario);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token))
                .isEqualTo(usuario.getUsername());
    }

    @Test
    void deveRetornarFalsoQuandoUsuarioDoTokenForDiferente() {
        String token = jwtService.generateToken(usuario);

        UserDetails outroUsuario = User
                .withUsername("outro@teste.com")
                .password("senha-ficticia")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid(token, outroUsuario))
                .isFalse();
    }
}