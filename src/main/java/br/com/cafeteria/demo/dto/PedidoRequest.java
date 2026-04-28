package br.com.cafeteria.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequest {
    private String clienteNome;
    private String clienteTelefone;
    private List<ItemRequest> itens;
}