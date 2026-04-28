package br.com.cafeteria.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardDTO {

    private BigDecimal totalVendasHoje;
    private BigDecimal totalVendasMes;
    private long totalPedidosHoje;
    private long totalPedidosPendentes;
    private long totalClientes;
    private BigDecimal ticketMedio;
    private List<ProdutoVendidoDTO> produtosMaisVendidos;
    private Map<LocalDate, BigDecimal> vendasPorDia;
    private List<PedidoRecenteDTO> pedidosRecentes;

    // Getters e Setters
    public BigDecimal getTotalVendasHoje() { return totalVendasHoje; }
    public void setTotalVendasHoje(BigDecimal totalVendasHoje) { this.totalVendasHoje = totalVendasHoje; }

    public BigDecimal getTotalVendasMes() { return totalVendasMes; }
    public void setTotalVendasMes(BigDecimal totalVendasMes) { this.totalVendasMes = totalVendasMes; }

    public long getTotalPedidosHoje() { return totalPedidosHoje; }
    public void setTotalPedidosHoje(long totalPedidosHoje) { this.totalPedidosHoje = totalPedidosHoje; }

    public long getTotalPedidosPendentes() { return totalPedidosPendentes; }
    public void setTotalPedidosPendentes(long totalPedidosPendentes) { this.totalPedidosPendentes = totalPedidosPendentes; }

    public long getTotalClientes() { return totalClientes; }
    public void setTotalClientes(long totalClientes) { this.totalClientes = totalClientes; }

    public BigDecimal getTicketMedio() { return ticketMedio; }
    public void setTicketMedio(BigDecimal ticketMedio) { this.ticketMedio = ticketMedio; }

    public List<ProdutoVendidoDTO> getProdutosMaisVendidos() { return produtosMaisVendidos; }
    public void setProdutosMaisVendidos(List<ProdutoVendidoDTO> produtosMaisVendidos) { this.produtosMaisVendidos = produtosMaisVendidos; }

    public Map<LocalDate, BigDecimal> getVendasPorDia() { return vendasPorDia; }
    public void setVendasPorDia(Map<LocalDate, BigDecimal> vendasPorDia) { this.vendasPorDia = vendasPorDia; }

    public List<PedidoRecenteDTO> getPedidosRecentes() { return pedidosRecentes; }
    public void setPedidosRecentes(List<PedidoRecenteDTO> pedidosRecentes) { this.pedidosRecentes = pedidosRecentes; }

    // DTOs internos
    public static class ProdutoVendidoDTO {
        private String nome;
        private long quantidade;
        private BigDecimal total;

        public ProdutoVendidoDTO(String nome, long quantidade, BigDecimal total) {
            this.nome = nome;
            this.quantidade = quantidade;
            this.total = total;
        }
        // Getters...
        public String getNome() { return nome; }
        public long getQuantidade() { return quantidade; }
        public BigDecimal getTotal() { return total; }
    }

    public static class PedidoRecenteDTO {
        private Long id;
        private String cliente;
        private BigDecimal total;
        private String status;
        private String horario;

        public PedidoRecenteDTO(Long id, String cliente, BigDecimal total, String status, String horario) {
            this.id = id;
            this.cliente = cliente;
            this.total = total;
            this.status = status;
            this.horario = horario;
        }
        // Getters...
        public Long getId() { return id; }
        public String getCliente() { return cliente; }
        public BigDecimal getTotal() { return total; }
        public String getStatus() { return status; }
        public String getHorario() { return horario; }
    }
}