package br.com.cafeteria.demo.dto;

import lombok.Data;

@Data
public class ItemRequest {
    private Long produtoId;
    private Integer quantidade;
}