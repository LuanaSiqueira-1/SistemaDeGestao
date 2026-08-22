package com.concessionaria.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.concessionaria.backend.dto.ClienteCadastroRequest;
import com.concessionaria.backend.dto.ClienteDetalheResponse;
import com.concessionaria.backend.dto.ClienteListagemResponse;
import com.concessionaria.backend.dto.ClienteResponse;
import com.concessionaria.backend.dto.HistoricoCompraResponse;
import com.concessionaria.backend.service.ClienteService;
import com.concessionaria.backend.service.VendaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final VendaService vendaService;

    public ClienteController(ClienteService clienteService, VendaService vendaService) {
        this.clienteService = clienteService;
        this.vendaService = vendaService;
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

    @GetMapping("/{id}/historico-compras")
    public ResponseEntity<List<HistoricoCompraResponse>> buscarHistoricoCompras(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(vendaService.buscarHistoricoCompras(id));
    }
}
