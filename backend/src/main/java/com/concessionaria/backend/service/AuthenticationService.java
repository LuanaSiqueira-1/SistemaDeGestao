package com.concessionaria.backend.service;

import com.concessionaria.backend.dto.RegisterResponse;
import com.concessionaria.backend.dto.RegisterRequest;
import com.concessionaria.backend.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.concessionaria.backend.exception.EmailJaCadastradoException;

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

    public RegisterResponse cadastrar(RegisterRequest request) {
        String emailNormalizado = request.email().trim().toLowerCase();

        if (userService.emailJaCadastrado(emailNormalizado)) {
        	throw new EmailJaCadastradoException();
        }

        User user = new User(
                null,
                request.nome().trim(),
                emailNormalizado,
                passwordEncoder.encode(request.senha()),
                request.role()
        );

        User usuarioSalvo = userService.salvar(user);

        return new RegisterResponse(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getRole()
        );
    }
}