package br.com.cafeteria.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CriarPedidoRequest {

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String clienteNome;

    @NotBlank(message = "Telefone é obrigatório")
    private String clienteTelefone;

    @NotEmpty(message = "Pedido deve ter pelo menos 1 item")
    @Valid
    private List<ItemRequest> itens;

    private String email; // Opcional - se não informar, usa lógica padrão
}