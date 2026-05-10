package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PedidoRepository - Testes")
class PedidoRepositoryTest {

    @Autowired private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar um pedido")
    void deveSalvarPedido() {
        Pedido pedido = Pedido.builder()
                .clienteNome("Maria Teste")
                .clienteTelefone("81988888888")
                .status(StatusPedido.PENDENTE)
                .total(new BigDecimal("25.00"))
                .itens(new ArrayList<>())
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();

        Pedido salvo = pedidoRepository.save(pedido);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getClienteNome()).isEqualTo("Maria Teste");
        assertThat(salvo.getStatus()).isEqualTo(StatusPedido.PENDENTE);
    }

    @Test
    @DisplayName("Deve buscar pedidos por status")
    void deveBuscarPedidosPorStatus() {
        Pedido pedido = Pedido.builder()
                .clienteNome("João Teste")
                .clienteTelefone("81999999999")
                .status(StatusPedido.ENTREGUE)
                .total(new BigDecimal("15.80"))
                .itens(new ArrayList<>())
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();
        pedidoRepository.save(pedido);

        var pedidos = pedidoRepository.findByStatus(StatusPedido.ENTREGUE);

        assertThat(pedidos).hasSize(1);
        assertThat(pedidos.get(0).getStatus()).isEqualTo(StatusPedido.ENTREGUE);
    }

    @Test
    @DisplayName("Deve buscar pedidos ordenados por data (mais recente primeiro)")
    void deveBuscarPedidosOrdenadosPorData() {
        Pedido pedido1 = Pedido.builder()
                .clienteNome("Cliente 1")
                .clienteTelefone("81911111111")
                .status(StatusPedido.PENDENTE)
                .total(new BigDecimal("10.00"))
                .itens(new ArrayList<>())
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();
        pedidoRepository.save(pedido1);

        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        Pedido pedido2 = Pedido.builder()
                .clienteNome("Cliente 2")
                .clienteTelefone("81922222222")
                .status(StatusPedido.PRONTO)
                .total(new BigDecimal("20.00"))
                .itens(new ArrayList<>())
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();
        pedidoRepository.save(pedido2);

        var pedidos = pedidoRepository.findByOrderByDataCriacaoDesc();

        assertThat(pedidos).hasSize(2);
        assertThat(pedidos.get(0).getClienteNome()).isEqualTo("Cliente 2");
        assertThat(pedidos.get(1).getClienteNome()).isEqualTo("Cliente 1");
    }

    @Test
    @DisplayName("Deve buscar pedidos entre datas de retirada")
    void deveBuscarPedidosEntreDatas() {
        LocalDateTime agora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime amanha = agora.plusDays(1);

        Pedido pedido = Pedido.builder()
                .clienteNome("Cliente Agendado")
                .clienteTelefone("81933333333")
                .status(StatusPedido.PENDENTE)
                .total(new BigDecimal("30.00"))
                .itens(new ArrayList<>())
                .horarioRetirada(agora.plusHours(2))
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();
        pedidoRepository.save(pedido);

        var pedidos = pedidoRepository.findByHorarioRetiradaBetween(agora, amanha);

        assertThat(pedidos).hasSize(1);
        assertThat(pedidos.get(0).getClienteNome()).isEqualTo("Cliente Agendado");
    }
}