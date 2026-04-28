package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar por status
    List<Pedido> findByStatus(StatusPedido status);

    // Buscar ordenado por data (mais recente primeiro)
    List<Pedido> findByOrderByDataCriacaoDesc();

    // Buscar pedidos entre datas (para agendamento)
    List<Pedido> findByHorarioRetiradaBetween(LocalDateTime inicio, LocalDateTime fim);
}