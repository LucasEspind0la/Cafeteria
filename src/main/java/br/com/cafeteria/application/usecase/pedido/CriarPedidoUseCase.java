package br.com.cafeteria.application.usecase.pedido;

import br.com.cafeteria.domain.exception.EstoqueInsuficienteException;
import br.com.cafeteria.domain.exception.ProdutoNaoEncontradoException;
import br.com.cafeteria.demo.model.*;
import br.com.cafeteria.demo.repository.PedidoRepository;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import br.com.cafeteria.domain.service.notification.Notificador;
import br.com.cafeteria.application.dto.request.CriarPedidoRequest;
import br.com.cafeteria.application.dto.request.ItemRequest;
import br.com.cafeteria.application.dto.response.PedidoResponse;
import br.com.cafeteria.application.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * UseCase para criação de pedidos - SRP
 * Responsabilidade única: criar um pedido válido com estoque atualizado
 */
@Service
@RequiredArgsConstructor
public class CriarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final Notificador notificador;
    private final PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponse executar(CriarPedidoRequest request) {
        Pedido pedido = inicializarPedido(request);
        BigDecimal total = processarItens(pedido, request);

        pedido.setTotal(total);
        Pedido salvo = pedidoRepository.save(pedido);

        notificarCliente(request, salvo);

        return pedidoMapper.toResponse(salvo);
    }

    private Pedido inicializarPedido(CriarPedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setClienteNome(request.getClienteNome());
        pedido.setClienteTelefone(request.getClienteTelefone());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setItens(new ArrayList<>());
        return pedido;
    }

    private BigDecimal processarItens(Pedido pedido, CriarPedidoRequest request) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemRequest itemReq : request.getItens()) {
            Produto produto = buscarProduto(itemReq.getProdutoId());
            validarEstoque(produto, itemReq.getQuantidade());

            ItemPedido item = criarItem(produto, itemReq.getQuantidade());
            pedido.getItens().add(item);

            total = total.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
            atualizarEstoque(produto, itemReq.getQuantidade());
        }

        return total;
    }

    private Produto buscarProduto(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));
    }

    private void validarEstoque(Produto produto, int quantidade) {
        if (produto.getEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(produto.getNome(), produto.getEstoque());
        }
    }

    private ItemPedido criarItem(Produto produto, int quantidade) {
        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        return item;
    }

    private void atualizarEstoque(Produto produto, int quantidade) {
        produto.setEstoque(produto.getEstoque() - quantidade);
        produtoRepository.save(produto);
    }

    private void notificarCliente(CriarPedidoRequest request, Pedido pedido) {
        String email = request.getEmail() != null
                ? request.getEmail()
                : gerarEmailPadrao(pedido.getClienteNome());

        notificador.notificarPedidoRecebido(email, pedido.getId());
    }

    private String gerarEmailPadrao(String nomeCliente) {
        return nomeCliente.toLowerCase()
                .replaceAll("[^a-z0-9]", ".")
                .replaceAll("\\.+", ".")
                + "@email.com";
    }
}