package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.dto.DashboardDTO;
import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import br.com.cafeteria.demo.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public DashboardDTO gerarDashboard() {
        DashboardDTO dto = new DashboardDTO();

        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimHoje = hoje.atTime(LocalTime.MAX);
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();

        List<Pedido> todosPedidos = pedidoRepository.findAll();

        // Filtra pedidos de hoje
        List<Pedido> pedidosHoje = todosPedidos.stream()
                .filter(p -> p.getDataCriacao() != null &&
                        !p.getDataCriacao().isBefore(inicioHoje) &&
                        !p.getDataCriacao().isAfter(fimHoje))
                .collect(Collectors.toList());

        List<Pedido> pedidosMes = todosPedidos.stream()
                .filter(p -> p.getDataCriacao() != null &&
                        !p.getDataCriacao().isBefore(inicioMes))
                .collect(Collectors.toList());

        // Totais (usando getTotal() do Lombok)
        dto.setTotalVendasHoje(somarTotais(pedidosHoje));
        dto.setTotalVendasMes(somarTotais(pedidosMes));
        dto.setTotalPedidosHoje(pedidosHoje.size());

        // Pedidos pendentes
        dto.setTotalPedidosPendentes(
                todosPedidos.stream()
                        .filter(p -> p.getStatus() == StatusPedido.PENDENTE)
                        .count()
        );

        // Clientes únicos
        dto.setTotalClientes(todosPedidos.stream()
                .map(Pedido::getClienteNome)
                .filter(Objects::nonNull)
                .distinct()
                .count());

        // Ticket médio
        BigDecimal totalGeral = somarTotais(todosPedidos);
        BigDecimal ticketMedio = todosPedidos.isEmpty() ? BigDecimal.ZERO :
                totalGeral.divide(BigDecimal.valueOf(todosPedidos.size()), 2, RoundingMode.HALF_UP);
        dto.setTicketMedio(ticketMedio);

        // Produtos mais vendidos (simulado)
        dto.setProdutosMaisVendidos(Arrays.asList(
                new DashboardDTO.ProdutoVendidoDTO("Café Expresso", 45, new BigDecimal("225.00")),
                new DashboardDTO.ProdutoVendidoDTO("Cappuccino", 32, new BigDecimal("272.00")),
                new DashboardDTO.ProdutoVendidoDTO("Croissant", 28, new BigDecimal("168.00"))
        ));

        // Vendas por dia (últimos 7 dias)
        Map<LocalDate, BigDecimal> vendasPorDia = new LinkedHashMap<>();
        for(int i=6; i>=0; i--) {
            LocalDate dia = hoje.minusDays(i);
            final LocalDate dataFiltro = dia;
            BigDecimal totalDia = somarTotais(
                    todosPedidos.stream()
                            .filter(p -> p.getDataCriacao() != null &&
                                    p.getDataCriacao().toLocalDate().equals(dataFiltro))
                            .collect(Collectors.toList())
            );
            vendasPorDia.put(dia, totalDia);
        }
        dto.setVendasPorDia(vendasPorDia);

        // Pedidos recentes (usando getTotal() do Lombok)
        dto.setPedidosRecentes(todosPedidos.stream()
                .sorted(Comparator.comparing(Pedido::getDataCriacao,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(p -> new DashboardDTO.PedidoRecenteDTO(
                        p.getId(),
                        p.getClienteNome() != null ? p.getClienteNome() : "Cliente",
                        p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO,
                        p.getStatus() != null ? p.getStatus().toString() : "PENDENTE",
                        p.getDataCriacao() != null ? p.getDataCriacao().toString() : "-"
                ))
                .collect(Collectors.toList()));

        return dto;
    }

    // Soma os totais dos pedidos (trata null)
    private BigDecimal somarTotais(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(p -> p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}