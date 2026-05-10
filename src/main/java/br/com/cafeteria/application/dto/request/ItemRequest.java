package br.com.cafeteria.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequest {

    @NotNull(message = "Produto é obrigatório")
    private Long produtoId;

    @Min(value = 1, message = "Quantidade mínima é 1")
    private Integer quantidade;
}