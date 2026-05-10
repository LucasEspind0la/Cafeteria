package br.com.cafeteria.domain.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(Long id) {
        super("Produto não encontrado: " + id);
    }
}