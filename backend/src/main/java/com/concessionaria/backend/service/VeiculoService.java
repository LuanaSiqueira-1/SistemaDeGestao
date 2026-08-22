package com.concessionaria.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.concessionaria.backend.dto.VeiculoCadastroRequest;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VeiculoUpdateDTO;
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

    /**
     * Laysa - Responsável pela implementação da US10 (Edição de veículo)
     */
    public VeiculoResponse atualizar(Long id, VeiculoUpdateDTO dto) {
        // 1. Localiza o veículo existente ou lança erro coerente se não encontrado (Q5-06)
        Veiculo veiculoExistente = veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com o ID: " + id));

        // 2. Regra de Consistência (Q5-07): Se o estado atual no banco for VENDIDO, 
        // a edição manual não pode forçar a volta para DISPONÍVEL silenciosamente.
        if (veiculoExistente.getStatus() == StatusVeiculo.VENDIDO && dto.getStatus() != StatusVeiculo.VENDIDO) {
            throw new RuntimeException("Regra de Negócio: Não é permitido alterar manualmente o status de um veículo já VENDIDO.");
        }

        // 3. Atualiza os campos permitidos pelo cadastro vigente (Q5-06)
        veiculoExistente.setMarca(dto.getMarca());
        veiculoExistente.setModelo(dto.getModelo());
        veiculoExistente.setAno(dto.getAno());
        veiculoExistente.setCor(dto.getCor());
        veiculoExistente.setQuilometragem(dto.getQuilometragem());
        veiculoExistente.setPreco(dto.getPreco());
        veiculoExistente.setStatus(dto.getStatus());

        // 4. Persiste a atualização válida no banco de dados
        Veiculo veiculoSalvo = veiculoRepository.save(veiculoExistente);

        // 5. Retorna a representação suficiente conforme o padrão do contrato (Q5-06)
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