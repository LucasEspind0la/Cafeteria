package br.com.cafeteria.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
public class VendasPorDiaResponse {
    private Map<LocalDate, BigDecimal> vendas;
}