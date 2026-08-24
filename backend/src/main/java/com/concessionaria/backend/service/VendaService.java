package com.concessionaria.backend.service;

import java.util.List;

import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.model.Venda;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.repository.ClienteRepository;
import com.concessionaria.backend.repository.VeiculoRepository;
import com.concessionaria.backend.repository.VendaRepository;
import com.concessionaria.backend.exception.ClienteNaoEncontradoException;
import com.concessionaria.backend.exception.VendaNaoEncontradaException; // ◄ IMPORTADO AQUI!

// Imports exatos dos DTOs do projeto
import com.concessionaria.backend.dto.VendaRequestDTO;
import com.concessionaria.backend.dto.VendaResponseDTO;
import com.concessionaria.backend.dto.VendaListagemResponse;
import com.concessionaria.backend.dto.VendaDetalheResponse;
import com.concessionaria.backend.dto.ClienteListagemResponse;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.ClienteDetalheResponse;
import com.concessionaria.backend.dto.HistoricoCompraResponse;
import com.concessionaria.backend.dto.HistoricoCompraResponse.VeiculoHistoricoResponse;
import com.concessionaria.backend.dto.VeiculoResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository, VeiculoRepository veiculoRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
    }

    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO dto) {
        // 1. Busca e valida o cliente
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(dto.clienteId()));

        // 2. Busca e valida o veículo
        Veiculo veiculo = veiculoRepository.findById(dto.veiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo com ID " + dto.veiculoId() + " não encontrado."));

        // 3. Valida se o veículo está disponível
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) { 
            throw new IllegalStateException("Este veículo não está disponível para venda. Status atual: " + veiculo.getStatus());
        }

        // 4. Atualiza o status do veículo para VENDIDO
        veiculo.setStatus(StatusVeiculo.VENDIDO); 
        veiculoRepository.save(veiculo);

        // 5. Salva a nova venda no banco
        Venda novaVenda = new Venda(null, dto.dataVenda(), dto.valor(), veiculo, cliente);
        Venda vendaSalva = vendaRepository.save(novaVenda);

        // 6. Retorna usando o construtor inteligente do próprio VendaResponseDTO
        return new VendaResponseDTO(vendaSalva);
    }

    @Transactional(readOnly = true)
    public VendaDetalheResponse buscarPorId(Long id) {

        // CORRIGIDO: Agora lançando a exceção que o JUnit espera, sem quebrar seus DTOs!
     Venda venda = vendaRepository.findById(id)
        .orElseThrow(() -> new VendaNaoEncontradaException(id));
        
        Cliente cliente = venda.getCliente();
        Veiculo veiculo = venda.getVeiculo();
        
        // Passando os atributos soltos exatamente na ordem exigida por ClienteDetalheResponse
        ClienteDetalheResponse clienteDTO = new ClienteDetalheResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail()
        );
        
        // Passando os atributos soltos exatamente na ordem exigida por VeiculoResponse
        VeiculoResponse veiculoDTO = new VeiculoResponse(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno(),
                veiculo.getPreco(),
                veiculo.getStatus()
        );
        
        return new VendaDetalheResponse(
                venda.getId(),
                venda.getDataVenda(),
                venda.getValor(),
                clienteDTO,
                veiculoDTO
        );
    }

    @Transactional(readOnly = true)
    public Page<VendaListagemResponse> listar(String cliente, String veiculo, Pageable pageable) {
        Page<Venda> paginaVendas = vendaRepository.pesquisar(cliente, veiculo, pageable);
        
        return paginaVendas.map(venda -> {
            Cliente c = venda.getCliente();
            Veiculo v = venda.getVeiculo();
            
            // Passando os atributos soltos exatamente na ordem exigida por ClienteListagemResponse
            ClienteListagemResponse clienteDTO = new ClienteListagemResponse(
                    c.getId(),
                    c.getNome(),
                    c.getCpf()
            );
            
            // Passando os atributos soltos exatamente na ordem exigida por VeiculoListagemResponse
            VeiculoListagemResponse veiculoDTO = new VeiculoListagemResponse(
                    v.getId(),
                    v.getMarca(),
                    v.getModelo(),
                    v.getAno(),
                    v.getPreco(),
                    v.getStatus()
            );
            
            return new VendaListagemResponse(
                    venda.getId(),
                    venda.getDataVenda(),
                    venda.getValor(),
                    clienteDTO,
                    veiculoDTO
            );
        });
    }

    @Transactional(readOnly = true)
    public List<HistoricoCompraResponse> buscarHistoricoCompras(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNaoEncontradoException(clienteId);
        }

        return vendaRepository.findByClienteIdOrderByDataVendaDesc(clienteId)
                .stream()
                .map(venda -> {
                    Veiculo veiculo = venda.getVeiculo();

                    return new HistoricoCompraResponse(
                            new VeiculoHistoricoResponse(
                                    veiculo.getId(),
                                    veiculo.getMarca(),
                                    veiculo.getModelo(),
                                    veiculo.getAno()
                            ),
                            venda.getDataVenda(),
                            venda.getValor()
                    );
                })
                .toList();
    }
}
