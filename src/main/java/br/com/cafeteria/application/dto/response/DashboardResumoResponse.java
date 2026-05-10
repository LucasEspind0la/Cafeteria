package br.com.cafeteria.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO especializado para resumo do dashboard
 * Apenas os dados necessários para o card de resumo
 */
@Data
@Builder
public class DashboardResumoResponse {
    private BigDecimal totalVendasHoje;
    private BigDecimal totalVendasMes;
    private long totalPedidosHoje;
    private long totalPedidosPendentes;
    private long totalClientes;
    private BigDecimal ticketMedio;
}