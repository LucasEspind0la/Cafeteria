package br.com.cafeteria.demo.controller;

import br.com.cafeteria.demo.dto.DashboardDTO;
import br.com.cafeteria.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // GET /api/dashboard → Dados completos do dashboard
    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.gerarDashboard());
    }
}