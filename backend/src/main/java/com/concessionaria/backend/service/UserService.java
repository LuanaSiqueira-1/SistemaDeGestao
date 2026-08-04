package com.concessionaria.backend.service;

import com.concessionaria.backend.model.User;
import com.concessionaria.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean emailJaCadastrado(String email) {
        return userRepository.existsByEmail(email);
    }

    public User salvar(User user) {
        return userRepository.save(user);
    }
}