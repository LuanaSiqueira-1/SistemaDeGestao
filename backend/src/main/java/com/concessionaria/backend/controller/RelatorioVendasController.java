package com.concessionaria.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.concessionaria.backend.dto.RelatorioVendasResponse;
import com.concessionaria.backend.service.RelatorioVendasService;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioVendasController {

    private final RelatorioVendasService relatorioVendasService;

    public RelatorioVendasController(
            RelatorioVendasService relatorioVendasService
    ) {
        this.relatorioVendasService = relatorioVendasService;
    }

    @GetMapping("/vendas")
    public ResponseEntity<RelatorioVendasResponse> gerar(
            @RequestParam int ano,
            @RequestParam(required = false) Integer semestre,
            @RequestParam(required = false) String marca
    ) {
        validarParametros(ano, semestre);

        return ResponseEntity.ok(
                relatorioVendasService.gerar(ano, semestre, marca)
        );
    }

    private void validarParametros(int ano, Integer semestre) {
        if (ano <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ano deve ser maior que zero."
            );
        }

        if (semestre != null && semestre != 1 && semestre != 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O semestre deve ser 1 ou 2."
            );
        }
    }
}
