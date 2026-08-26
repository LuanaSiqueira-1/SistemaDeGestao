package com.concessionaria.backend.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.concessionaria.backend.dto.AuthResponse;
import com.concessionaria.backend.dto.LoginRequest;
import com.concessionaria.backend.dto.RegisterRequest;
import com.concessionaria.backend.dto.RegisterResponse;
import com.concessionaria.backend.exception.EmailJaCadastradoException;
import com.concessionaria.backend.model.Role;
import com.concessionaria.backend.model.User;
import com.concessionaria.backend.repository.UserRepository;
import com.concessionaria.backend.security.JwtService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    void configurar() {
        authenticationService = new AuthenticationService(
                userService,
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager
        );
    }

    @Test
    void cadastroPublicoDeveSempreCriarUsuarioComRoleUser() {
        RegisterRequest request = new RegisterRequest(
                "  Marina Oliveira  ",
                "MARINA@TESTE.COM",
                "senha123"
        );

        when(
                userService.emailJaCadastrado(
                        "marina@teste.com"
                )
        ).thenReturn(false);

        when(passwordEncoder.encode("senha123"))
                .thenReturn("senha-codificada");

        when(userService.salvar(any(User.class)))
                .thenAnswer(invocacao -> {
                    User usuario =
                            invocacao.getArgument(0);

                    usuario.setId(1L);

                    return usuario;
                });

        RegisterResponse response =
                authenticationService.cadastrar(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userService).salvar(captor.capture());

        User usuarioSalvo = captor.getValue();

        assertThat(usuarioSalvo.getNome())
                .isEqualTo("Marina Oliveira");

        assertThat(usuarioSalvo.getEmail())
                .isEqualTo("marina@teste.com");

        assertThat(usuarioSalvo.getSenha())
                .isEqualTo("senha-codificada");

        assertThat(usuarioSalvo.getRole())
                .isEqualTo(Role.USER);

        assertThat(response.id())
                .isEqualTo(1L);

        assertThat(response.role())
                .isEqualTo(Role.USER);
    }

    @Test
    void naoDeveCadastrarQuandoEmailJaExistir() {
        RegisterRequest request = new RegisterRequest(
                "Marina Oliveira",
                "marina@teste.com",
                "senha123"
        );

        when(
                userService.emailJaCadastrado(
                        "marina@teste.com"
                )
        ).thenReturn(true);

        assertThatThrownBy(
                () -> authenticationService.cadastrar(request)
        ).isInstanceOf(
                EmailJaCadastradoException.class
        );

        verify(userService, never())
                .salvar(any(User.class));

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void loginDeveContinuarRetornandoJwtEDadosDoUsuario() {
        User usuario = new User(
                1L,
                "Marina Oliveira",
                "marina@teste.com",
                "senha-codificada",
                Role.USER
        );

        when(
                userRepository.findByEmail(
                        "marina@teste.com"
                )
        ).thenReturn(Optional.of(usuario));

        when(jwtService.generateToken(usuario))
                .thenReturn("token-jwt");

        LoginRequest request = new LoginRequest(
                "marina@teste.com",
                "senha123"
        );

        AuthResponse response =
                authenticationService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken>
                autenticacaoCaptor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager)
                .authenticate(
                        autenticacaoCaptor.capture()
                );

        UsernamePasswordAuthenticationToken autenticacao =
                autenticacaoCaptor.getValue();

        assertThat(autenticacao.getPrincipal())
                .isEqualTo("marina@teste.com");

        assertThat(autenticacao.getCredentials())
                .isEqualTo("senha123");

        assertThat(response.getToken())
                .isEqualTo("token-jwt");

        assertThat(response.getEmail())
                .isEqualTo("marina@teste.com");

        assertThat(response.getRole())
                .isEqualTo(Role.USER);
    }
}