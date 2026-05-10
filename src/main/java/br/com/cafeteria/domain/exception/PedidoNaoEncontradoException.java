package br.com.cafeteria.domain.exception;

public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException(Long id) {
        super("Pedido não encontrado: " + id);
    }
}