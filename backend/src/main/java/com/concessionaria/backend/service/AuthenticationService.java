package com.concessionaria.backend.service;

import com.concessionaria.backend.dto.AuthResponse;
import com.concessionaria.backend.dto.LoginRequest;
import com.concessionaria.backend.dto.RegisterRequest;
import com.concessionaria.backend.dto.RegisterResponse;
import com.concessionaria.backend.exception.EmailJaCadastradoException;
import com.concessionaria.backend.model.Role;
import com.concessionaria.backend.model.User;
import com.concessionaria.backend.repository.UserRepository;
import com.concessionaria.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserService userService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponse cadastrar(RegisterRequest request) {
        String emailNormalizado =
                request.getEmail().trim().toLowerCase();

        if (userService.emailJaCadastrado(emailNormalizado)) {
            throw new EmailJaCadastradoException();
        }

        User user = new User(
                null,
                request.getNome().trim(),
                emailNormalizado,
                passwordEncoder.encode(request.getSenha()),
                Role.USER
        );

        User usuarioSalvo = userService.salvar(user);

        return new RegisterResponse(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(
                jwtToken,
                user.getNome(),
                user.getEmail(),
                user.getRole()
        );
    }
}