package com.concessionaria.backend;


import com.concessionaria.backend.model.Role;
import com.concessionaria.backend.model.User;
import com.concessionaria.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)

class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void deveSalvarBuscarEVerificarUsuarioPorEmail(){
        String email ="rianna.teste@ufape.edu.br";

        User usuario = new User(
                null,
                "Rianna",
                email,
                "senhaTeste",
                Role.USER
        );
        User usuarioSalvo = userRepository.saveAndFlush(usuario);
        Optional<User> usuarioEncontrado =
                userRepository.findByEmail(email);
        boolean emailExiste =
                userRepository.existsByEmail(email);

        assertThat(usuarioSalvo.getId()).isNotNull();
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getEmail())
                .isEqualTo(email);
        assertThat(emailExiste).isTrue();
    }

    @Test
    void deveAtualizarUsuario() {
        User usuario = new User(
                null,
                "Rianna",
                "rianna.atualizacao@ufape.edu.br",
                "senhaTeste",
                Role.USER
        );

        User usuarioSalvo = userRepository.saveAndFlush(usuario);

        usuarioSalvo.setNome("Rianna Atualizada");
        usuarioSalvo.setRole(Role.ADMIN);

        userRepository.saveAndFlush(usuarioSalvo);

        User usuarioAtualizado = userRepository
                .findById(usuarioSalvo.getId())
                .orElseThrow();

        assertThat(usuarioAtualizado.getNome())
                .isEqualTo("Rianna Atualizada");
        assertThat(usuarioAtualizado.getRole())
                .isEqualTo(Role.ADMIN);
    }
}
