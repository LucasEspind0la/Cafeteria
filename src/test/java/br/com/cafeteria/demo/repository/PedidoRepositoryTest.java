package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("PedidoRepository - Testes de Integração")
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void limparDados() {
        // Limpa TODOS os pedidos e itens relacionados
        entityManager.getEntityManager()
                .createNativeQuery("DELETE FROM itens_pedido")
                .executeUpdate();
        entityManager.getEntityManager()
                .createNativeQuery("DELETE FROM pagamentos")
                .executeUpdate();
        entityManager.getEntityManager()
                .createNativeQuery("DELETE FROM notificacoes")
                .executeUpdate();
        entityManager.getEntityManager()
                .createNativeQuery("DELETE FROM pedidos")
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve buscar pedidos por status corretamente")
    void deveBuscarPedidosPorStatus() {
        // DEBUG: Verifica se há pedidos antes de inserir
        List<Pedido> pedidosAntes = pedidoRepository.findAll();
        System.out.println("Pedidos antes: " + pedidosAntes.size());
        for (Pedido p : pedidosAntes) {
            System.out.println("  - " + p.getClienteNome() + " | " + p.getStatus());
        }

        // Arrange: Cria pedidos com diferentes status
        Pedido pedidoPendente1 = criarPedido("João", StatusPedido.PENDENTE);
        Pedido pedidoPendente2 = criarPedido("Ana", StatusPedido.PENDENTE);
        Pedido pedidoPronto = criarPedido("Maria", StatusPedido.PRONTO);

        entityManager.persist(pedidoPendente1);
        entityManager.persist(pedidoPendente2);
        entityManager.persist(pedidoPronto);
        entityManager.flush();

        // Act
        List<Pedido> pedidosPendentes = pedidoRepository.findByStatus(StatusPedido.PENDENTE);
        List<Pedido> pedidosProntos = pedidoRepository.findByStatus(StatusPedido.PRONTO);
        List<Pedido> pedidosCancelados = pedidoRepository.findByStatus(StatusPedido.CANCELADO);

        // Assert
        assertEquals(2, pedidosPendentes.size(), "Deve retornar 2 pedidos PENDENTE");
        assertEquals(1, pedidosProntos.size(), "Deve retornar 1 pedido PRONTO");
        assertTrue(pedidosCancelados.isEmpty(), "Deve retornar lista vazia para CANCELADO");
    }

    private Pedido criarPedido(String nome, StatusPedido status) {
        Pedido pedido = new Pedido();
        pedido.setClienteNome(nome);
        pedido.setClienteTelefone("11999999999");
        pedido.setStatus(status);
        pedido.setTotal(new BigDecimal("10.00"));
        pedido.setDataCriacao(LocalDateTime.now());
        return pedido;
    }
}