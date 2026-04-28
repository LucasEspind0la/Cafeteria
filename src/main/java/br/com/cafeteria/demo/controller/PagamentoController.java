package br.com.cafeteria.demo.controller;

import br.com.cafeteria.demo.dto.PagamentoRequest;
import br.com.cafeteria.demo.model.Pagamento;
import br.com.cafeteria.demo.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    // POST /api/pagamentos/criar → Cria intenção de pagamento
    @PostMapping("/criar")
    public ResponseEntity<?> criarPagamento(@RequestBody PagamentoRequest request) {
        try {
            Map<String, String> resposta = pagamentoService.criarPagamento(
                    request.getPedidoId(),
                    request.getClienteEmail(),
                    request.getValor(),
                    request.getMetodo()
            );
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    // POST /api/pagamentos/simular → Pagamento de teste (sem Stripe)
    @PostMapping("/simular")
    public ResponseEntity<Pagamento> simularPagamento(@RequestBody PagamentoRequest request) {
        Pagamento pagamento = pagamentoService.simularPagamento(
                request.getPedidoId(),
                request.getClienteEmail(),
                request.getValor()
        );
        return ResponseEntity.ok(pagamento);
    }

    // POST /api/pagamentos/confirmar/{paymentIntentId}
    @PostMapping("/confirmar/{paymentIntentId}")
    public ResponseEntity<Pagamento> confirmar(@PathVariable String paymentIntentId) {
        Pagamento pagamento = pagamentoService.confirmarPagamento(paymentIntentId);
        return ResponseEntity.ok(pagamento);
    }
}