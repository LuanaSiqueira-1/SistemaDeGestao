package com.concessionaria.backend.dto;

import java.math.BigDecimal;

import com.concessionaria.backend.model.StatusVeiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class VeiculoUpdateDTO {

    @NotBlank(message = "A marca não pode estar em branco")
    private String marca;

    @NotBlank(message = "O modelo não pode estar em branco")
    private String modelo;

    @NotNull(message = "O ano não pode ser nulo")
    private Integer ano;

    private String cor;

    @PositiveOrZero(message = "A quilometragem não pode ser negativa")
    private Long quilometragem;

    @NotNull(message = "O preço não pode ser nulo")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "O status não pode ser nulo")
    private StatusVeiculo status;

    // --- Getters e Setters ---

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Long getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(Long quilometragem) {
        this.quilometragem = quilometragem;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }
}