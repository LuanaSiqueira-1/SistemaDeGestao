package com.concessionaria.backend.service;

import org.springframework.stereotype.Service;

import com.concessionaria.backend.dto.ClienteAtualizacaoRequest;
import com.concessionaria.backend.dto.ClienteCadastroRequest;
import com.concessionaria.backend.dto.ClienteDetalheResponse;
import com.concessionaria.backend.dto.ClienteListagemResponse;
import com.concessionaria.backend.dto.ClienteResponse;
import com.concessionaria.backend.exception.ClienteNaoEncontradoException;
import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse cadastrar(ClienteCadastroRequest request) {

        Cliente cliente = new Cliente();

        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteResponse(
                clienteSalvo.getId(),
                clienteSalvo.getNome(),
                clienteSalvo.getCpf(),
                clienteSalvo.getTelefone(),
                clienteSalvo.getEmail()
        );
    }

    public Page<ClienteListagemResponse> listar(
            String nome,
            String cpf,
            Pageable pageable
    ) {
        String nomeFiltro = nome == null ? "" : nome.trim();
        String cpfFiltro = cpf == null ? "" : cpf.trim();

        return clienteRepository
                .findByNomeContainingIgnoreCaseAndCpfContaining(
                        nomeFiltro,
                        cpfFiltro,
                        pageable
                )
                .map(this::paraListagemResponse);
    }

    public ClienteDetalheResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        return paraDetalheResponse(cliente);
    }

    public ClienteResponse atualizar(Long id, ClienteAtualizacaoRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteResponse(
                clienteSalvo.getId(),
                clienteSalvo.getNome(),
                clienteSalvo.getCpf(),
                clienteSalvo.getTelefone(),
                clienteSalvo.getEmail()
        );
    }

    private ClienteListagemResponse paraListagemResponse(Cliente cliente) {
        return new ClienteListagemResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf()
        );
    }

    private ClienteDetalheResponse paraDetalheResponse(Cliente cliente) {
        return new ClienteDetalheResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail()
        );
    }
}
