package com.concessionaria.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.concessionaria.backend.model.Venda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByClienteIdOrderByDataVendaDesc(Long clienteId);

    @Query("""
            SELECT venda
            FROM Venda venda
            WHERE (
                :cliente = ''
                OR LOWER(venda.cliente.nome) LIKE LOWER(CONCAT('%', :cliente, '%'))
                OR venda.cliente.cpf LIKE CONCAT('%', :cliente, '%')
            )
            AND (
                :veiculo = ''
                OR LOWER(venda.veiculo.marca) LIKE LOWER(CONCAT('%', :veiculo, '%'))
                OR LOWER(venda.veiculo.modelo) LIKE LOWER(CONCAT('%', :veiculo, '%'))
            )
            """)

    Page<Venda> pesquisar(
            @Param("cliente") String cliente,
            @Param("veiculo") String veiculo,
            Pageable pageable
    );
}
