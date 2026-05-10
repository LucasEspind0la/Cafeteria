package br.com.cafeteria.infrastructure.notification;

import br.com.cafeteria.domain.service.notification.Notificador;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Notificador para ambiente de produção
 * Aqui você integraria com JavaMailSender ou serviço de email real
 */
@Slf4j
@Component
@Profile("!dev")
public class SmtpEmailNotificador implements Notificador {

    @Override
    public void notificarPedidoRecebido(String destinatario, Long pedidoId) {
        // TODO: Integrar com JavaMailSender futuramente
        log.info("[EMAIL] Pedido #{} recebido. Enviando para: {}", pedidoId, destinatario);
    }

    @Override
    public void notificarPedidoPronto(String destinatario, Long pedidoId) {
        log.info("[EMAIL] Pedido #{} pronto! Enviando para: {}", pedidoId, destinatario);
    }

    @Override
    public void notificarPagamentoConfirmado(String destinatario, Long pedidoId) {
        log.info("[EMAIL] Pagamento do pedido #{} confirmado. Enviando para: {}", pedidoId, destinatario);
    }
}