package br.com.cafeteria.demo.unit.service;

import br.com.cafeteria.demo.dto.ItemRequest;
import br.com.cafeteria.demo.dto.PedidoRequest;
import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.model.StatusPedido;
import br.com.cafeteria.demo.repository.PedidoRepository;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import br.com.cafeteria.demo.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Produto cafe;
    private Produto croissant;

    @BeforeEach
    void setUp() {
        cafe = new Produto();
        cafe.setId(1L);
        cafe.setNome("Café");
        cafe.setPreco(new BigDecimal("5.00"));
        cafe.setEstoque(10);

        croissant = new Produto();
        croissant.setId(2L);
        croissant.setNome("Croissant");
        croissant.setPreco(new BigDecimal("6.00"));
        croissant.setEstoque(5);
    }

    @Test
    void deveCriarPedidoComSucesso() {
        // Given
        ItemRequest item1 = new ItemRequest();
        item1.setProdutoId(1L);
        item1.setQuantidade(2);

        ItemRequest item2 = new ItemRequest();
        item2.setProdutoId(2L);
        item2.setQuantidade(1);

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("João");
        request.setClienteTelefone("11999999999");
        request.setItens(Arrays.asList(item1, item2));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(cafe));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(croissant));
        when(produtoRepository.save(any())).thenReturn(cafe, croissant);
        when(pedidoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        var pedido = pedidoService.criarPedido(request);

        // Then
        assertNotNull(pedido);
        assertEquals("João", pedido.getClienteNome());
        assertEquals(StatusPedido.PENDENTE, pedido.getStatus());
        assertEquals(new BigDecimal("16.00"), pedido.getTotal()); // 2x5 + 1x6 = 16
        assertEquals(2, pedido.getItens().size());

        // Verifica que estoque foi decrementado
        assertEquals(8, cafe.getEstoque());  // 10 - 2
        assertEquals(4, croissant.getEstoque()); // 5 - 1
    }

    @Test
    void deveLancarErroQuandoEstoqueInsuficiente() {
        // Given
        ItemRequest item = new ItemRequest();
        item.setProdutoId(1L);
        item.setQuantidade(20); // Mais que o estoque (10)

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("Maria");
        request.setClienteTelefone("11888888888");
        request.setItens(List.of(item));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(cafe));

        // When + Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.criarPedido(request);
        });

        assertEquals("Estoque insuficiente: Café", exception.getMessage());
    }
}