package br.com.cafeteria.application.usecase.dashboard;

import br.com.cafeteria.demo.repository.PedidoRepository;
import br.com.cafeteria.application.dto.response.VendasPorDiaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UseCase para gerar vendas por período - OCP
 * Extensível: pode adicionar novos períodos sem modificar esta classe
 */
@Service
@RequiredArgsConstructor
public class GerarVendasPorPeriodoUseCase {

    private final PedidoRepository pedidoRepository;

    public VendasPorDiaResponse executar(int dias) {
        LocalDate hoje = LocalDate.now();
        Map<LocalDate, BigDecimal> vendas = new LinkedHashMap<>();

        for (int i = dias - 1; i >= 0; i--) {
            LocalDate dia = hoje.minusDays(i);
            LocalDateTime dataHora = dia.atStartOfDay();
            BigDecimal total = pedidoRepository.sumTotalByDataCriacaoDate(dataHora);
            vendas.put(dia, total != null ? total : BigDecimal.ZERO);
        }

        return new VendasPorDiaResponse(vendas);
    }
}