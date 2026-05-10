package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByOrderByDataCriacaoDesc();
    List<Pedido> findByHorarioRetiradaBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.dataCriacao BETWEEN :inicio AND :fim")
    BigDecimal sumTotalByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE FUNCTION('DATE', p.dataCriacao) = FUNCTION('DATE', :data)")
    BigDecimal sumTotalByDataCriacaoDate(LocalDateTime data);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.status = 'PENDENTE'")
    long countPendentes();

    @Query("SELECT COUNT(DISTINCT p.clienteTelefone) FROM Pedido p")
    long countClientesUnicos();
}