package com.concessionaria.backend.service;

import org.springframework.stereotype.Service;

import com.concessionaria.backend.dto.ClienteCadastroRequest;
import com.concessionaria.backend.dto.ClienteResponse;
import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse cadastrar(ClienteCadastroRequest request) {

        Cliente cliente = new Cliente();

        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteResponse(
                clienteSalvo.getId(),
                clienteSalvo.getNome(),
                clienteSalvo.getCpf(),
                clienteSalvo.getTelefone(),
                clienteSalvo.getEmail()
        );
    }
}
