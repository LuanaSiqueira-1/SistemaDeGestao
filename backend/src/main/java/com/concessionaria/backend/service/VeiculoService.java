package com.concessionaria.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.repository.VeiculoRepository;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    // Ricardo - responsável pela consulta/listagem de veículos.
    public List<VeiculoListagemResponse> listarVeiculos() {

        // Busca todos os veículos cadastrados no banco.
        return veiculoRepository.findAll()
                .stream()

                // Converte cada veículo para o formato definido no contrato da listagem.
                .map(veiculo -> new VeiculoListagemResponse(
                        veiculo.getId(),
                        veiculo.getMarca(),
                        veiculo.getModelo(),
                        veiculo.getAno(),
                        veiculo.getPreco(),
                        veiculo.getStatus()
                ))
                .toList();
    }
}