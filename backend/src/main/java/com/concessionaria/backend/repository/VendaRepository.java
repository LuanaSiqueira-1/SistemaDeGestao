package com.concessionaria.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concessionaria.backend.model.Venda;


public interface VendaRepository extends JpaRepository<Venda, Long> {
}