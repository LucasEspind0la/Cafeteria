package br.com.cafeteria.application.mapper;

import br.com.cafeteria.demo.model.ItemPedido;
import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.application.dto.response.PedidoResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .clienteTelefone(pedido.getClienteTelefone())
                .status(pedido.getStatus())
                .total(pedido.getTotal())
                .dataCriacao(pedido.getDataCriacao())
                .itens(toItemResponses(pedido.getItens()))
                .build();
    }

    private List<PedidoResponse.ItemPedidoResponse> toItemResponses(List<ItemPedido> itens) {
        if (itens == null) return List.of();

        return itens.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    private PedidoResponse.ItemPedidoResponse toItemResponse(ItemPedido item) {
        BigDecimal subtotal = item.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));

        return PedidoResponse.ItemPedidoResponse.builder()
                .produtoNome(item.getProduto().getNome())
                .quantidade(item.getQuantidade())
                .precoUnitario(item.getPrecoUnitario())
                .subtotal(subtotal)
                .build();
    }
}