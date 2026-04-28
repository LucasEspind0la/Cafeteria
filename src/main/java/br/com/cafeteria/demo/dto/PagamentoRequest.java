package br.com.cafeteria.demo.dto;

import java.math.BigDecimal;

public class PagamentoRequest {
    private Long pedidoId;
    private String clienteEmail;
    private BigDecimal valor;
    private String metodo; // CARTAO, PIX

    // Getters e Setters
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
}