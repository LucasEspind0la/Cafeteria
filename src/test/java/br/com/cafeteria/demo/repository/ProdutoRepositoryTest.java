package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProdutoRepository - Testes")
class ProdutoRepositoryTest {

    @Autowired private ProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar um produto")
    void deveSalvarProduto() {
        Produto produto = Produto.builder()
                .nome("Latte")
                .tipo("Bebida")
                .categoria("Cafés")
                .preco(new BigDecimal("9.90"))
                .estoque(30)
                .descricao("Café com leite")
                .imagem("http://exemplo.com/latte.jpg")
                .build();

        Produto salvo = produtoRepository.save(produto);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Latte");
        assertThat(salvo.getTipo()).isEqualTo("Bebida");
    }

    @Test
    @DisplayName("Deve buscar produto por id")
    void deveBuscarProdutoPorId() {
        Produto produto = Produto.builder()
                .nome("Mocha")
                .tipo("Bebida")
                .categoria("Cafés")
                .preco(new BigDecimal("11.00"))
                .estoque(15)
                .descricao("Chocolate com café")
                .imagem("http://exemplo.com/mocha.jpg")
                .build();
        produto = produtoRepository.save(produto);

        Optional<Produto> encontrado = produtoRepository.findById(produto.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getPreco()).isEqualTo(new BigDecimal("11.00"));
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosProdutos() {
        Produto produto1 = Produto.builder()
                .nome("Café Preto")
                .tipo("Bebida")
                .categoria("Cafés")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .descricao("Café puro")
                .imagem("http://exemplo.com/preto.jpg")
                .build();

        Produto produto2 = Produto.builder()
                .nome("Suco de Laranja")
                .tipo("Bebida")
                .categoria("Sucos")
                .preco(new BigDecimal("8.00"))
                .estoque(20)
                .descricao("Suco natural")
                .imagem("http://exemplo.com/suco.jpg")
                .build();

        produtoRepository.save(produto1);
        produtoRepository.save(produto2);

        var produtos = produtoRepository.findAll();

        assertThat(produtos).hasSize(2);
    }

    @Test
    @DisplayName("Deve deletar um produto")
    void deveDeletarProduto() {
        Produto produto = Produto.builder()
                .nome("Chá Gelado")
                .tipo("Bebida")
                .categoria("Chás")
                .preco(new BigDecimal("6.50"))
                .estoque(25)
                .descricao("Chá refrescante")
                .imagem("http://exemplo.com/cha.jpg")
                .build();
        produto = produtoRepository.save(produto);

        produtoRepository.deleteById(produto.getId());

        Optional<Produto> deletado = produtoRepository.findById(produto.getId());
        assertThat(deletado).isEmpty();
    }
}