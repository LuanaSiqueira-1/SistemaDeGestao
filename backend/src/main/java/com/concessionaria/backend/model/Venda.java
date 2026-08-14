package com.concessionaria.backend.model; // Ajustado para o pacote real do projeto

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "tb_vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data da venda é obrigatória.")
    @Column(name = "data_venda", nullable = false)
    private LocalDate dataVenda;

    @NotNull(message = "O valor da venda é obrigatório.")
    @Positive(message = "O valor da venda deve ser maior que zero.")
    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    // Relacionamento com Veículo (US08)
    @NotNull(message = "O veículo associado à venda é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    // Relacionamento com Cliente real (Sem classe fantasma!)
    @NotNull(message = "O cliente associado à venda é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Venda() {
    }

    public Venda(Long id, LocalDate dataVenda, BigDecimal valor, Veiculo veiculo, Cliente cliente) {
        this.id = id;
        this.dataVenda = dataVenda;
        this.valor = valor;
        this.veiculo = veiculo;
        this.cliente = cliente;
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDate dataVenda) { this.dataVenda = dataVenda; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}