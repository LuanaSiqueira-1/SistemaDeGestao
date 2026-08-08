package com.concessionaria.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.concessionaria.backend.dto.VeiculoCadastroRequest;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.repository.VeiculoRepository;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public VeiculoResponse cadastrar(VeiculoCadastroRequest request) {

        Veiculo veiculo = new Veiculo();

        veiculo.setMarca(request.marca());
        veiculo.setModelo(request.modelo());
        veiculo.setAno(request.ano());
        veiculo.setCor(request.cor());
        veiculo.setQuilometragem(request.quilometragem());
        veiculo.setPreco(request.preco());
        veiculo.setStatus(request.status());

        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);

        return new VeiculoResponse(
                veiculoSalvo.getId(),
                veiculoSalvo.getMarca(),
                veiculoSalvo.getModelo(),
                veiculoSalvo.getAno(),
                veiculoSalvo.getPreco(),
                veiculoSalvo.getStatus()
        );
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