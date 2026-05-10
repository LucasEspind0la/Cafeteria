package br.com.cafeteria.domain.service.notification;

/**
 * Interface para notificações - DIP
 * O domínio define o contrato, não se importa com como é implementado
 */
public interface Notificador {
    void notificarPedidoRecebido(String destinatario, Long pedidoId);
    void notificarPedidoPronto(String destinatario, Long pedidoId);
    void notificarPagamentoConfirmado(String destinatario, Long pedidoId);
}