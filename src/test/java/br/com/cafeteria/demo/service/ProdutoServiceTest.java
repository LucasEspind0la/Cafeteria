package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(1L)
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
    void testListarTodos() {
        // Arrange
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(produto));

        // Act
        List<Produto> resultado = produtoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Cafe Expresso", resultado.get(0).getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void testSalvarProduto() {
        // Arrange
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        doNothing().when(produtoRepository).flush();

        // Act
        Produto resultado = produtoService.salvar(produto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Cafe Expresso", resultado.getNome());
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(produtoRepository, times(1)).flush();
    }

    @Test
    void testSalvarProdutoSemNome() {
        // Arrange
        produto.setNome(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.salvar(produto);
        });
        assertEquals("Nome do produto e obrigatorio", exception.getMessage());
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act
        Produto resultado = produtoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Cafe Expresso", resultado.getNome());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void testAtualizarProduto() {
        // Arrange
        Produto produtoAtualizado = Produto.builder()
                .nome("Cafe Latte")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("7.00"))
                .estoque(20)
                .descricao("Cafe com leite")
                .imagem("https://exemplo.com/latte.jpg")
                .build();

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        doNothing().when(produtoRepository).flush();

        // Act
        Produto resultado = produtoService.atualizar(1L, produtoAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(produtoRepository, times(1)).flush();
    }

    @Test
    void testDeletarProduto() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        doNothing().when(produtoRepository).deleteById(1L);

        // Act
        produtoService.deletar(1L);

        // Assert
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).deleteById(1L);
    }
}