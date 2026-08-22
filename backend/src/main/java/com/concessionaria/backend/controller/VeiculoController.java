package com.concessionaria.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concessionaria.backend.dto.VeiculoCadastroRequest;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VeiculoUpdateDTO;
import com.concessionaria.backend.service.VeiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    public ResponseEntity<VeiculoResponse> cadastrar(
            @Valid @RequestBody VeiculoCadastroRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(veiculoService.cadastrar(request));
    }

    // Ricardo - endpoint responsável pela consulta dos veículos cadastrados.
    @GetMapping
    public ResponseEntity<List<VeiculoListagemResponse>> listarVeiculos() {
        return ResponseEntity.ok(veiculoService.listarVeiculos());
    }

    /**
     * Laysa - Endpoint responsável pela US10 (Edição de veículo)
     */
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody VeiculoUpdateDTO dto
    ) {
        VeiculoResponse response = veiculoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }
}