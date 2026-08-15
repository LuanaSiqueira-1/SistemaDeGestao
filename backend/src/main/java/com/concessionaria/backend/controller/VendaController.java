package com.concessionaria.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concessionaria.backend.dto.VendaRequestDTO;
import com.concessionaria.backend.dto.VendaResponseDTO;
import com.concessionaria.backend.service.VendaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*") // Permite que o Angular do Ricardo acesse sem erro de CORS
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> registrarVenda(@RequestBody @Valid VendaRequestDTO dto) {
        VendaResponseDTO response = vendaService.registrarVenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}