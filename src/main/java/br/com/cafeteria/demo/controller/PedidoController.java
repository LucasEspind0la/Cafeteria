package br.com.cafeteria.demo.controller;

import br.com.cafeteria.demo.dto.PedidoRequest;
import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import br.com.cafeteria.demo.service.NotificacaoService;
import br.com.cafeteria.demo.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final NotificacaoService notificacaoService;

    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criarPedido(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> listarPorStatus(@PathVariable StatusPedido status) {
        return ResponseEntity.ok(pedidoService.listarPorStatus(status));
    }

    @GetMapping("/ativos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> listarAtivos() {
        List<StatusPedido> ativos = List.of(StatusPedido.PENDENTE, StatusPedido.PREPARO, StatusPedido.PRONTO);
        List<Pedido> pedidos = pedidoService.listarTodos().stream()
                .filter(p -> ativos.contains(p.getStatus()))
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/hoje")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> listarHoje() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        List<Pedido> pedidos = pedidoService.listarTodos().stream()
                .filter(p -> p.getDataCriacao() != null && p.getDataCriacao().toLocalDate().equals(hoje))
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pedido> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status) {
        Pedido pedido = pedidoService.atualizarStatus(id, status);
        if (status == StatusPedido.PRONTO) {
            notificacaoService.notificarPedidoPronto(pedido.getClienteNome() + "@email.com", id);
        }
        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{id}/pagar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarPagamento(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Pedido pedido = pedidoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
            pedido.setStatus(StatusPedido.PAGO);
            Pedido atualizado = pedidoService.atualizarStatus(id, StatusPedido.PAGO);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}