package com.concessionaria.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.concessionaria.backend.model.Cliente;
import com.concessionaria.backend.model.StatusVeiculo;
import com.concessionaria.backend.model.Veiculo;
import com.concessionaria.backend.model.Venda;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class VendaRepositoryRelatorioTest {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    private Venda vendaToyotaJaneiro;
    private Venda vendaHondaJunho;
    private Venda vendaToyotaJulho;

    @BeforeEach
    void criarDadosDoTeste() {
        Cliente cliente = salvarCliente();
        Veiculo toyota = salvarVeiculo("Toyota", "Corolla");
        Veiculo honda = salvarVeiculo("Honda", "Civic");

        salvarVenda(
                LocalDate.of(2025, 12, 31),
                "90000.00",
                toyota,
                cliente
        );

        vendaToyotaJaneiro = salvarVenda(
                LocalDate.of(2026, 1, 1),
                "100000.00",
                toyota,
                cliente
        );

        vendaHondaJunho = salvarVenda(
                LocalDate.of(2026, 6, 30),
                "110000.00",
                honda,
                cliente
        );

        vendaToyotaJulho = salvarVenda(
                LocalDate.of(2026, 7, 1),
                "120000.00",
                toyota,
                cliente
        );

        salvarVenda(
                LocalDate.of(2027, 1, 1),
                "130000.00",
                toyota,
                cliente
        );

        vendaRepository.flush();
    }

    @Test
    void deveFiltrarPrimeiroSemestrePorMarca() {
        List<Venda> resultado = vendaRepository.buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 7, 1),
                "toyota"
        );

        assertThat(resultado)
                .extracting(Venda::getId)
                .containsExactly(vendaToyotaJaneiro.getId());
    }

    @Test
    void deveBuscarAnoInteiroQuandoMarcaEstiverVazia() {
        List<Venda> resultado = vendaRepository.buscarParaRelatorio(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                ""
        );

        assertThat(resultado)
                .extracting(Venda::getId)
                .containsExactlyInAnyOrder(
                        vendaToyotaJaneiro.getId(),
                        vendaHondaJunho.getId(),
                        vendaToyotaJulho.getId()
                );
    }

    private Cliente salvarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente dos indicadores");
        cliente.setCpf("12345678901");
        cliente.setTelefone("81999999999");
        cliente.setEmail("indicadores@teste.com");

        return clienteRepository.saveAndFlush(cliente);
    }

    private Veiculo salvarVeiculo(String marca, String modelo) {
        Veiculo veiculo = new Veiculo();
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);
        veiculo.setAno(2024);
        veiculo.setCor("Prata");
        veiculo.setQuilometragem(0L);
        veiculo.setPreco(new BigDecimal("100000.00"));
        veiculo.setStatus(StatusVeiculo.VENDIDO);

        return veiculoRepository.saveAndFlush(veiculo);
    }

    private Venda salvarVenda(
            LocalDate data,
            String valor,
            Veiculo veiculo,
            Cliente cliente
    ) {
        Venda venda = new Venda(
                null,
                data,
                new BigDecimal(valor),
                veiculo,
                cliente
        );

        return vendaRepository.save(venda);
    }
}