package br.com.cafeteria.demo.controller;

import br.com.cafeteria.demo.dto.PedidoRequest;
import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.StatusPedido;
import br.com.cafeteria.demo.service.NotificacaoService;
import br.com.cafeteria.demo.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final NotificacaoService notificacaoService;

    // POST /api/pedidos → Criar pedido
    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criarPedido(request));
    }

    // GET /api/pedidos → Listar todos
    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // GET /api/pedidos/{id} → Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/pedidos/status/{status} → Filtrar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pedido>> listarPorStatus(@PathVariable StatusPedido status) {
        return ResponseEntity.ok(pedidoService.listarPorStatus(status));
    }

    // PUT /api/pedidos/{id}/status → Atualizar status
    @PutMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status) {
        Pedido pedido = pedidoService.atualizarStatus(id, status);

        if (status == StatusPedido.PRONTO) {
            notificacaoService.notificarPedidoPronto(
                    pedido.getClienteNome() + "@email.com",
                    id
            );
        }

        return ResponseEntity.ok(pedido);
    }
}