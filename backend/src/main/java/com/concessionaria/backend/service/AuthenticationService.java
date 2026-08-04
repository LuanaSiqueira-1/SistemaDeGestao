package com.concessionaria.backend.service;

import com.concessionaria.backend.dto.AuthResponse;
import com.concessionaria.backend.dto.RegisterRequest;
import com.concessionaria.backend.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse cadastrar(RegisterRequest request) {
        String emailNormalizado = request.email().trim().toLowerCase();

        if (userService.emailJaCadastrado(emailNormalizado)) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        User user = new User(
                null,
                request.nome().trim(),
                emailNormalizado,
                passwordEncoder.encode(request.senha()),
                request.role()
        );

        User usuarioSalvo = userService.salvar(user);

        return new AuthResponse(
                null,
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getRole()
        );
    }
}