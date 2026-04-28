package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.dto.ItemRequest;
import br.com.cafeteria.demo.dto.PedidoRequest;
import br.com.cafeteria.demo.model.ItemPedido;
import br.com.cafeteria.demo.model.Pedido;
import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.model.StatusPedido;
import br.com.cafeteria.demo.repository.PedidoRepository;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // Criar novo pedido
    public Pedido criarPedido(PedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setClienteNome(request.getClienteNome());
        pedido.setClienteTelefone(request.getClienteTelefone());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setItens(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        for (ItemRequest itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemReq.getProdutoId()));

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            pedido.getItens().add(item);

            total = total.add(produto.getPreco().multiply(new BigDecimal(itemReq.getQuantidade())));
        }

        pedido.setTotal(total);

        Pedido salvo = pedidoRepository.save(pedido);

        // Envia notificação
        notificacaoService.notificarPedidoRecebido(
                request.getClienteNome() + "@email.com",
                salvo.getId()
        );

        return salvo;
    }

    // Listar todos
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // Buscar por ID
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // Listar por status
    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    // Atualizar status
    public Pedido atualizarStatus(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));

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