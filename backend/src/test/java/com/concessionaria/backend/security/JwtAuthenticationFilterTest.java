package com.concessionaria.backend.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void configurar() {
        jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveContinuarQuandoCabecalhoNaoForBearer() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Basic credencial"
        );

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void deveContinuarQuandoTokenNaoPossuirEmail() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer token-sem-email"
        );

        when(jwtService.extractUsername("token-sem-email"))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isNull();

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void deveManterAutenticacaoQuandoContextoJaEstiverPreenchido()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer token-valido"
        );

        UsernamePasswordAuthenticationToken autenticacaoExistente =
                new UsernamePasswordAuthenticationToken(
                        "usuario-existente",
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(autenticacaoExistente);

        when(jwtService.extractUsername("token-valido"))
                .thenReturn("marina.oliveira@teste.com");

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isSameAs(autenticacaoExistente);

        verify(userDetailsService, never())
                .loadUserByUsername(
                        "marina.oliveira@teste.com"
                );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveContinuarSemAutenticarQuandoTokenNaoForValido()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer token-expirado"
        );

        UserDetails usuario = User
                .withUsername("marina.oliveira@teste.com")
                .password("senha-ficticia")
                .roles("USER")
                .build();

        when(jwtService.extractUsername("token-expirado"))
                .thenReturn("marina.oliveira@teste.com");

        when(userDetailsService.loadUserByUsername(
                "marina.oliveira@teste.com"
        )).thenReturn(usuario);

        when(jwtService.isTokenValid(
                "token-expirado",
                usuario
        )).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isNull();

        verify(filterChain).doFilter(request, response);
    }
}