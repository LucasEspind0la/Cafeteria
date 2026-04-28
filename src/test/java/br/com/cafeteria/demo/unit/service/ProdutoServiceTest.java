package br.com.cafeteria.demo.unit.service;

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import br.com.cafeteria.demo.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest  // ✅ Carrega contexto Spring (mais fácil)
class ProdutoServiceTest {

    @MockBean  // ✅ Mock gerenciado pelo Spring
    private ProdutoRepository repository;

    @Autowired  // ✅ Injeta o service real
    private ProdutoService service;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Café Expresso");
        produto.setTipo("Bebida");
        produto.setCategoria("Quente");
        produto.setPreco(new BigDecimal("5.00"));
        produto.setEstoque(50);
        produto.setDescricao("Café forte");
    }

    @Test
    void deveListarTodosOsProdutos() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList(produto));

        // When
        List<Produto> resultado = service.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Café Expresso", resultado.get(0).getNome());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveBuscarProdutoPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Café Expresso", resultado.getNome());
    }

    @Test
    void deveCadastrarProduto() {
        when(repository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = service.cadastrar(produto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(repository).save(produto);
    }

    @Test
    void deveDeletarProduto() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deletar(1L));
        verify(repository).deleteById(1L);
    }
}