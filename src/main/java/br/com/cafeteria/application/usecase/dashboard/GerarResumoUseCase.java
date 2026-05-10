package br.com.cafeteria.application.usecase.dashboard;

import br.com.cafeteria.demo.repository.PedidoRepository;
import br.com.cafeteria.application.dto.response.DashboardResumoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * UseCase para gerar resumo do dashboard - SRP
 * Responsabilidade única: calcular métricas de resumo (vendas hoje, ticket médio, etc.)
 */
@Service
@RequiredArgsConstructor
public class GerarResumoUseCase {

    private final PedidoRepository pedidoRepository;

    public DashboardResumoResponse executar() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimHoje = hoje.atTime(LocalTime.MAX);
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();

        BigDecimal totalVendasHoje = pedidoRepository.sumTotalByDataCriacaoBetween(inicioHoje, fimHoje);
        BigDecimal totalVendasMes = pedidoRepository.sumTotalByDataCriacaoBetween(inicioMes, fimHoje);
        long totalPedidosHoje = pedidoRepository.findByDataCriacaoBetween(inicioHoje, fimHoje).size();
        long pendentes = pedidoRepository.countPendentes();
        long clientes = pedidoRepository.countClientesUnicos();
        BigDecimal ticketMedio = calcularTicketMedio();

        return DashboardResumoResponse.builder()
                .totalVendasHoje(totalVendasHoje != null ? totalVendasHoje : BigDecimal.ZERO)
                .totalVendasMes(totalVendasMes != null ? totalVendasMes : BigDecimal.ZERO)
                .totalPedidosHoje(totalPedidosHoje)
                .totalPedidosPendentes(pendentes)
                .totalClientes(clientes)
                .ticketMedio(ticketMedio)
                .build();
    }

    private BigDecimal calcularTicketMedio() {
        var todos = pedidoRepository.findAll();
        if (todos.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = todos.stream()
                .map(p -> p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(todos.size()), 2, RoundingMode.HALF_UP);
    }
}