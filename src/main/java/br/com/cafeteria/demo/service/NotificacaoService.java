package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.model.Notificacao;
import br.com.cafeteria.demo.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    // Enviar notificação genérica
    public Notificacao enviarNotificacao(String clienteEmail, String titulo, String mensagem, Notificacao.TipoNotificacao tipo) {
        Notificacao notificacao = new Notificacao(clienteEmail, titulo, mensagem, tipo);
        return notificacaoRepository.save(notificacao);
    }

    // Notificação simplificada
    public void enviarNotificacao(String clienteEmail, String mensagem) {
        enviarNotificacao(clienteEmail, "Cafeteria", mensagem, Notificacao.TipoNotificacao.PEDIDO_RECEBIDO);
    }

    // Listar notificações do cliente
    public List<Notificacao> listarNotificacoes(String clienteEmail) {
        return notificacaoRepository.findByClienteEmailOrderByDataEnvioDesc(clienteEmail);
    }

    // Não lidas
    public List<Notificacao> notificacoesNaoLidas(String clienteEmail) {
        return notificacaoRepository.findByClienteEmailAndLidaFalse(clienteEmail);
    }

    // Contar não lidas
    public long contarNaoLidas(String clienteEmail) {
        return notificacaoRepository.countByClienteEmailAndLidaFalse(clienteEmail);
    }

    // Marcar como lida
    public Notificacao marcarComoLida(Long notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }

    // === FLUXO DE PEDIDO ===

    public void notificarPedidoRecebido(String clienteEmail, Long pedidoId) {
        enviarNotificacao(clienteEmail, "Pedido Recebido!",
                "Seu pedido #" + pedidoId + " foi recebido e está sendo preparado.",
                Notificacao.TipoNotificacao.PEDIDO_RECEBIDO);
    }

    public void notificarPedidoPronto(String clienteEmail, Long pedidoId) {
        enviarNotificacao(clienteEmail, "Pedido Pronto!",
                "Seu pedido #" + pedidoId + " está pronto para retirada!",
                Notificacao.TipoNotificacao.PEDIDO_PRONTO);
    }
}