package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.dto.ItemRequest;
import br.com.cafeteria.demo.dto.PedidoRequest;
import br.com.cafeteria.demo.exception.ValidacaoException;
import br.com.cafeteria.demo.model.ItemPedido;
import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.model.StatusPedido;
import br.com.cafeteria.demo.repository.PedidoRepository;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final NotificacaoService notificacaoService;

    @Transactional
    public Pedido criarPedido(PedidoRequest request) {
        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new ValidacaoException("Pedido deve conter pelo menos um item");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteNome(request.getClienteNome());
        pedido.setClienteTelefone(request.getClienteTelefone());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setItens(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        for (ItemRequest itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new ValidacaoException("Produto não encontrado: " + itemReq.getProdutoId()));

            if (produto.getEstoque() < itemReq.getQuantidade()) {
                throw new ValidacaoException("Estoque insuficiente: " + produto.getNome());
            }

            produto.setEstoque(produto.getEstoque() - itemReq.getQuantidade());
            produtoRepository.save(produto);

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            pedido.getItens().add(item);

            total = total.add(produto.getPreco().multiply(new BigDecimal(itemReq.getQuantidade())));
        }

        pedido.setTotal(total);

        Pedido salvo = pedidoRepository.save(pedido);

        notificacaoService.notificarPedidoRecebido(
                request.getClienteNome() + "@email.com",
                salvo.getId()
        );

        return salvo;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Pedido não encontrado: " + id));

        pedido.setStatus(status);

        if (status == StatusPedido.PRONTO) {
            notificacaoService.notificarPedidoPronto(
                    pedido.getClienteNome() + "@email.com",
                    id
            );
        }

        return pedidoRepository.save(pedido);
    }
}