package com.concessionaria.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.concessionaria.backend.dto.EstoqueResumoResponse;
import com.concessionaria.backend.service.EstoqueService;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<EstoqueResumoResponse> resumo(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
                estoqueService.resumir(marca, status)
        );
    }
}
