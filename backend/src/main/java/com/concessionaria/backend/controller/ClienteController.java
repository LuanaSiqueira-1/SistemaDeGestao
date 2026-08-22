package com.concessionaria.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.concessionaria.backend.dto.ClienteAtualizacaoRequest;
import com.concessionaria.backend.dto.ClienteCadastroRequest;
import com.concessionaria.backend.dto.ClienteDetalheResponse;
import com.concessionaria.backend.dto.ClienteListagemResponse;
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

    @GetMapping
    public ResponseEntity<Page<ClienteListagemResponse>> listar(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "") String cpf,
            @PageableDefault(
                    size = 10,
                    sort = "nome",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(
                clienteService.listar(nome, cpf, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDetalheResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteAtualizacaoRequest request
    ) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }
}
