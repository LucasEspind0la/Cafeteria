package br.com.cafeteria.application.dto.response;

import br.com.cafeteria.demo.model.StatusPedido;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PedidoResponse {
    private Long id;
    private String clienteNome;
    private String clienteTelefone;
    private StatusPedido status;
    private BigDecimal total;
    private LocalDateTime dataCriacao;
    private List<ItemPedidoResponse> itens;

    @Data
    @Builder
    public static class ItemPedidoResponse {
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal subtotal;
    }
}