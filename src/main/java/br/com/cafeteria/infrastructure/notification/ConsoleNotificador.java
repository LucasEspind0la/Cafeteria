package br.com.cafeteria.infrastructure.notification;

import br.com.cafeteria.domain.service.notification.Notificador;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Notificador para ambiente de desenvolvimento
 * Apenas loga no console, não envia email real
 */
@Slf4j
@Component
@Profile("dev")
public class ConsoleNotificador implements Notificador {

    @Override
    public void notificarPedidoRecebido(String destinatario, Long pedidoId) {
        log.info("[DEV-NOTIFICAÇÃO] Pedido #{} recebido. Destinatário: {}", pedidoId, destinatario);
    }

    @Override
    public void notificarPedidoPronto(String destinatario, Long pedidoId) {
        log.info("[DEV-NOTIFICAÇÃO] Pedido #{} pronto! Destinatário: {}", pedidoId, destinatario);
    }

    @Override
    public void notificarPagamentoConfirmado(String destinatario, Long pedidoId) {
        log.info("[DEV-NOTIFICAÇÃO] Pagamento do pedido #{} confirmado. Destinatário: {}", pedidoId, destinatario);
    }
}