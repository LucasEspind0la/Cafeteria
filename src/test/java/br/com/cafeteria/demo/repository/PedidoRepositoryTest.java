package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("PedidoRepository - Testes de Integração")
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void limparDados() {
        pedidoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve buscar pedidos por status PENDENTE")
    void deveBuscarPedidosPendentes() {
        // Cria 2 pedidos (o @PrePersist seta como PENDENTE automaticamente)
        Pedido pedido1 = criarPedido("João");
        pedidoRepository.save(pedido1);

        Pedido pedido2 = criarPedido("Ana");
        pedidoRepository.save(pedido2);

        // Busca PENDENTES
        List<Pedido> pendentes = pedidoRepository.findByStatus(StatusPedido.PENDENTE);

        // Deve retornar 2
        assertThat(pendentes).hasSize(2);
    }

    @Test
    @DisplayName("Deve buscar pedidos por status PRONTO")
    void deveBuscarPedidosProntos() {
        // Cria pedido como PENDENTE (padrão do @PrePersist)
        Pedido pedido = criarPedido("Maria");
        pedido = pedidoRepository.save(pedido);

        // Altera para PRONTO e salva novamente
        pedido.setStatus(StatusPedido.PRONTO);
        pedidoRepository.save(pedido);

        // Busca PRONTOS
        List<Pedido> prontos = pedidoRepository.findByStatus(StatusPedido.PRONTO);

        // Deve retornar 1
        assertThat(prontos).hasSize(1);
        assertThat(prontos.get(0).getClienteNome()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para status sem pedidos")
    void deveRetornarVazioParaStatusSemPedidos() {
        // Cria um pedido PENDENTE
        Pedido pedido = criarPedido("João");
        pedidoRepository.save(pedido);

        // Busca CANCELADO (nenhum existe)
        List<Pedido> cancelados = pedidoRepository.findByStatus(StatusPedido.CANCELADO);

        // Deve retornar vazio
        assertThat(cancelados).isEmpty();
    }

    private Pedido criarPedido(String nome) {
        return Pedido.builder()
                .clienteNome(nome)
                .clienteTelefone("11999999999")
                .total(new BigDecimal("10.00"))
                .itens(new ArrayList<>())
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();
    }
}