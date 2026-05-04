package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProdutoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .nome("Cafe Expresso")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .descricao("Cafe forte e cremoso")
                .imagem("https://exemplo.com/cafe.jpg")
                .build();
    }

    @Test
    void testSalvarProdutoNoBanco() {
        // Act
        Produto salvo = produtoRepository.save(produto);
        produtoRepository.flush();

        // Assert
        assertNotNull(salvo.getId());
        assertTrue(salvo.getId() > 0);
        assertEquals("Cafe Expresso", salvo.getNome());

        // Verificar se realmente foi para o banco
        Produto doBanco = entityManager.find(Produto.class, salvo.getId());
        assertNotNull(doBanco);
        assertEquals("Cafe Expresso", doBanco.getNome());
    }

    @Test
    void testBuscarTodosProdutos() {
        // Arrange
        entityManager.persist(produto);
        entityManager.flush();

        // Act
        List<Produto> produtos = produtoRepository.findAll();

        // Assert
        assertFalse(produtos.isEmpty());
        assertTrue(produtos.stream().anyMatch(p -> p.getNome().equals("Cafe Expresso")));
    }

    @Test
    void testBuscarPorTipo() {
        // Arrange
        entityManager.persist(produto);
        entityManager.flush();

        // Act
        List<Produto> resultado = produtoRepository.findByTipo("Quente");

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals("Quente", resultado.get(0).getTipo());
    }

    @Test
    void testBuscarEstoqueBaixo() {
        // Arrange
        produto.setEstoque(3);
        entityManager.persist(produto);
        entityManager.flush();

        // Act
        List<Produto> resultado = produtoRepository.findByEstoqueLessThan(5);

        // Assert
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).getEstoque() < 5);
    }

    @Test
    void testAtualizarProduto() {
        // Arrange
        Produto salvo = entityManager.persistAndFlush(produto);
        salvo.setNome("Cafe Latte");
        salvo.setPreco(new BigDecimal("7.00"));

        // Act
        Produto atualizado = produtoRepository.save(salvo);
        produtoRepository.flush();

        // Assert
        assertEquals("Cafe Latte", atualizado.getNome());
        assertEquals(new BigDecimal("7.00"), atualizado.getPreco());

        // Verificar no banco
        Produto doBanco = entityManager.find(Produto.class, salvo.getId());
        assertEquals("Cafe Latte", doBanco.getNome());
    }

    @Test
    void testDeletarProduto() {
        // Arrange
        Produto salvo = entityManager.persistAndFlush(produto);
        Long id = salvo.getId();

        // Act
        produtoRepository.deleteById(id);
        produtoRepository.flush();

        // Assert
        Produto doBanco = entityManager.find(Produto.class, id);
        assertNull(doBanco);
    }
}