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
import com.concessionaria.backend.dto.VendaListagemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.concessionaria.backend.dto.VendaDetalheResponse;
import org.springframework.web.bind.annotation.PathVariable;

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
    @GetMapping
    public ResponseEntity<Page<VendaListagemResponse>> listar(
            @RequestParam(defaultValue = "") String cliente,
            @RequestParam(defaultValue = "") String veiculo,
            @PageableDefault(
                    size = 10,
                    sort = "dataVenda",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(
                vendaService.listar(cliente, veiculo, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaDetalheResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }
}