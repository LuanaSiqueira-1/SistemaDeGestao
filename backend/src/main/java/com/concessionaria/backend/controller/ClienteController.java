package com.concessionaria.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concessionaria.backend.dto.ClienteCadastroRequest;
import com.concessionaria.backend.dto.ClienteResponse;
import com.concessionaria.backend.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(
            @Valid @RequestBody ClienteCadastroRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.cadastrar(request));
    }
}
