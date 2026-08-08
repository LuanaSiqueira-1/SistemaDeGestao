package com.concessionaria.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.service.VeiculoService;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    // Ricardo - endpoint responsável pela consulta dos veículos cadastrados.
    @GetMapping
    public ResponseEntity<List<VeiculoListagemResponse>> listarVeiculos() {
        return ResponseEntity.ok(veiculoService.listarVeiculos());
    }
}