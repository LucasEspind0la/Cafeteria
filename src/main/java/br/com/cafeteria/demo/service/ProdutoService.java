package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    // LISTAR TODOS
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // BUSCAR POR ID
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    // CADASTRAR
    public Produto cadastrar(Produto produto) {
        return produtoRepository.save(produto);
    }

    // ATUALIZAR
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id);
        produto.setNome(produtoAtualizado.getNome());
        produto.setTipo(produtoAtualizado.getTipo());
        produto.setCategoria(produtoAtualizado.getCategoria());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setEstoque(produtoAtualizado.getEstoque());
        produto.setDescricao(produtoAtualizado.getDescricao());
        return produtoRepository.save(produto);
    }

    // DELETAR
    public void deletar(Long id) {
        buscarPorId(id); // Verifica se existe
        produtoRepository.deleteById(id);
    }

    // BUSCAR POR TIPO
    public List<Produto> buscarPorTipo(String tipo) {
        return produtoRepository.findByTipo(tipo);
    }

    // VERIFICAR ESTOQUE BAIXO
    public List<Produto> estoqueBaixo(Integer limite) {
        return produtoRepository.findByEstoqueLessThan(limite);
    }
}