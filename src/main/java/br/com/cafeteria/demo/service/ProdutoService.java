package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    // LISTAR TODOS
    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // BUSCAR POR ID
    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    // CADASTRAR / SALVAR
    @Transactional
    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);  // ← CORRIGIDO!
    }

    // ATUALIZAR
    @Transactional
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id);
        produto.setNome(produtoAtualizado.getNome());
        produto.setTipo(produtoAtualizado.getTipo());
        produto.setCategoria(produtoAtualizado.getCategoria());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setEstoque(produtoAtualizado.getEstoque());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setImagem(produtoAtualizado.getImagem());
        return produtoRepository.save(produto);
    }

    // DELETAR
    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        produtoRepository.deleteById(id);
    }

    // BUSCAR POR TIPO
    @Transactional(readOnly = true)
    public List<Produto> buscarPorTipo(String tipo) {
        return produtoRepository.findByTipo(tipo);
    }

    // VERIFICAR ESTOQUE BAIXO
    @Transactional(readOnly = true)
    public List<Produto> estoqueBaixo(Integer limite) {
        return produtoRepository.findByEstoqueLessThan(limite);
    }
}