package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.model.Pagamento;
import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.repository.PagamentoRepository;
import br.com.cafeteria.demo.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final NotificacaoService notificacaoService;

    // Simular pagamento (para testes)
    public Pagamento simularPagamento(Long pedidoId, String clienteEmail, BigDecimal valor) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        Pagamento pagamento = new Pagamento(pedido, clienteEmail, valor, "SIMULADO");
        pagamento.setStatus(Pagamento.StatusPagamento.APROVADO);
        pagamento.setDataConfirmacao(LocalDateTime.now());

        pagamentoRepository.save(pagamento);

        notificacaoService.enviarNotificacao(clienteEmail,
                "Pagamento confirmado! Pedido #" + pedidoId + " está sendo preparado.");

        return pagamento;
    }

    // Confirmar pagamento manualmente
    public Pagamento confirmarPagamento(String paymentIntentId) {
        Pagamento pagamento = pagamentoRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        pagamento.setStatus(Pagamento.StatusPagamento.APROVADO);
        pagamento.setDataConfirmacao(LocalDateTime.now());

        Pedido pedido = pagamento.getPedido();
        pedido.setStatus(br.com.cafeteria.demo.model.StatusPedido.PAGO);

        pagamentoRepository.save(pagamento);
        pedidoRepository.save(pedido);

        notificacaoService.enviarNotificacao(
                pagamento.getClienteEmail(),
                "Pagamento confirmado! Pedido #" + pedido.getId() + " está sendo preparado.");

        return pagamento;
    }

    // Criar intenção de pagamento (Stripe - opcional)
    public Map<String, String> criarPagamento(Long pedidoId, String clienteEmail, BigDecimal valor, String metodo) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // integraria com Stripe se quiser
        //  retorna simulação
        Map<String, String> resposta = new HashMap<>();
        resposta.put("simulado", "true");
        resposta.put("pedidoId", pedidoId.toString());

        return resposta;
    }
}