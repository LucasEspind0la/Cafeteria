package br.com.cafeteria.domain.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String produto, int estoqueAtual) {
        super(String.format("Estoque insuficiente para '%s'. Disponível: %d unidades", produto, estoqueAtual));
    }
}