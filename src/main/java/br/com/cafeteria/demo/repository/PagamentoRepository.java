package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByPedidoId(Long pedidoId);
    Optional<Pagamento> findByStripePaymentIntentId(String paymentIntentId);
}