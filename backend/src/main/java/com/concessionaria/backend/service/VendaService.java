package com.concessionaria.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.concessionaria.backend.dto.VendaRequestDTO;
import com.concessionaria.backend.dto.VendaResponseDTO;
import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.model.Venda;
import com.concessionaria.backend.repository.ClienteRepository;
import com.concessionaria.backend.repository.VeiculoRepository;
import com.concessionaria.backend.repository.VendaRepository;
import com.concessionaria.backend.dto.ClienteListagemResponse;
import com.concessionaria.backend.dto.VeiculoListagemResponse;
import com.concessionaria.backend.dto.VendaListagemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.concessionaria.backend.dto.ClienteDetalheResponse;
import com.concessionaria.backend.dto.VeiculoResponse;
import com.concessionaria.backend.dto.VendaDetalheResponse;
import com.concessionaria.backend.exception.VendaNaoEncontradaException;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    // Injeção de dependência por construtor (Boa prática que o SonarCloud exige)
    public VendaService(VendaRepository vendaRepository, 
                        ClienteRepository clienteRepository, 
                        VeiculoRepository veiculoRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
    }

    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO dto) {
        // 1. Busca o Cliente no banco (Garante que ele existe)
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + dto.clienteId()));

        // 2. Busca o Veículo no banco (Garante que ele existe)
        Veiculo veiculo = veiculoRepository.findById(dto.veiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com o ID: " + dto.veiculoId()));

        // 3. Cria a entidade Venda preenchida
        Venda novaVenda = new Venda();
        novaVenda.setDataVenda(dto.dataVenda());
        novaVenda.setValor(dto.valor());
        novaVenda.setCliente(cliente);
        novaVenda.setVeiculo(veiculo);

        // 4. Salva no banco de dados PostgreSQL
        Venda vendaSalva = vendaRepository.save(novaVenda);

        // 5. Retorna o DTO formatado para o Ricardo usar no Front
        return new VendaResponseDTO(vendaSalva);
    }

    @Transactional(readOnly = true)
    public Page<VendaListagemResponse> listar(
            String cliente,
            String veiculo,
            Pageable pageable
    ) {
        String clienteFiltro = cliente == null ? "" : cliente.trim();
        String veiculoFiltro = veiculo == null ? "" : veiculo.trim();

        return vendaRepository
                .pesquisar(clienteFiltro, veiculoFiltro, pageable)
                .map(this::paraListagemResponse);
    }

    private VendaListagemResponse paraListagemResponse(Venda venda) {
        Cliente cliente = venda.getCliente();
        Veiculo veiculo = venda.getVeiculo();

        ClienteListagemResponse clienteResponse =
                new ClienteListagemResponse(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getCpf()
                );

        VeiculoListagemResponse veiculoResponse =
                new VeiculoListagemResponse(
                        veiculo.getId(),
                        veiculo.getMarca(),
                        veiculo.getModelo(),
                        veiculo.getAno(),
                        veiculo.getPreco(),
                        veiculo.getStatus()
                );

        return new VendaListagemResponse(
                venda.getId(),
                venda.getDataVenda(),
                venda.getValor(),
                clienteResponse,
                veiculoResponse
        );
    }

    @Transactional(readOnly = true)
    public VendaDetalheResponse buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new VendaNaoEncontradaException(id));

        return paraDetalheResponse(venda);
    }

    private VendaDetalheResponse paraDetalheResponse(Venda venda) {
        Cliente cliente = venda.getCliente();
        Veiculo veiculo = venda.getVeiculo();

        ClienteDetalheResponse clienteResponse =
                new ClienteDetalheResponse(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getCpf(),
                        cliente.getTelefone(),
                        cliente.getEmail()
                );

        VeiculoResponse veiculoResponse =
                new VeiculoResponse(
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
                clienteResponse,
                veiculoResponse
        );
    }
}