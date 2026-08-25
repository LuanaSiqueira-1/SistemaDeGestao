package com.concessionaria.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.concessionaria.backend.dto.VeiculoCadastroRequest;
import com.concessionaria.backend.dto.VeiculoDetalheResponse;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VeiculoUpdateDTO;
import com.concessionaria.backend.exception.StatusVeiculoInvalidoException;
import com.concessionaria.backend.exception.VeiculoNaoEncontradoException;
import com.concessionaria.backend.model.StatusVeiculo;
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

        return veiculoRepository.findAll()
                .stream()
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

    /**
     * Consulta os dados completos de um veículo pelo ID.
     */
    public VeiculoDetalheResponse buscarPorId(Long id) {

        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(id));

        return new VeiculoDetalheResponse(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno(),
                veiculo.getCor(),
                veiculo.getQuilometragem(),
                veiculo.getPreco(),
                veiculo.getStatus()
        );
    }

    /**
     * Laysa - Responsável pela implementação da US10 (Edição de veículo)
     */
    public VeiculoResponse atualizar(Long id, VeiculoUpdateDTO dto) {

        // 1. Localiza o veículo ou retorna erro 404 pelo GlobalExceptionHandler
        Veiculo veiculoExistente = veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(id));

        // 2. Regra de consistência:
        // um veículo vendido não pode voltar manualmente para outro status
        if (veiculoExistente.getStatus() == StatusVeiculo.VENDIDO
                && dto.getStatus() != StatusVeiculo.VENDIDO) {

            throw new StatusVeiculoInvalidoException(
                    "Não é permitido alterar manualmente o status de um veículo já VENDIDO."
            );
        }

        // 3. Atualiza os campos
        veiculoExistente.setMarca(dto.getMarca());
        veiculoExistente.setModelo(dto.getModelo());
        veiculoExistente.setAno(dto.getAno());
        veiculoExistente.setCor(dto.getCor());
        veiculoExistente.setQuilometragem(dto.getQuilometragem());
        veiculoExistente.setPreco(dto.getPreco());
        veiculoExistente.setStatus(dto.getStatus());

        // 4. Persiste no banco
        Veiculo veiculoSalvo = veiculoRepository.save(veiculoExistente);

        // 5. Retorna os dados atualizados
        return new VeiculoResponse(
                veiculoSalvo.getId(),
                veiculoSalvo.getMarca(),
                veiculoSalvo.getModelo(),
                veiculoSalvo.getAno(),
                veiculoSalvo.getPreco(),
                veiculoSalvo.getStatus()
        );
    }
}