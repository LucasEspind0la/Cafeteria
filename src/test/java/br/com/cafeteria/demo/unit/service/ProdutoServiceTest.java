package br.com.cafeteria.demo.unit.service;

import br.com.cafeteria.demo.service.ProdutoService;
import br.com.cafeteria.domain.exception.ProdutoNaoEncontradoException;
import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService - Unitário")
class ProdutoServiceTest {

    @Mock private ProdutoRepository produtoRepository;
    @InjectMocks private ProdutoService service;

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        var produto = new Produto();
        produto.setId(1L);
        produto.setNome("Café");
        produto.setPreco(new BigDecimal("5.00"));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        var resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Café", resultado.getNome());
        verify(produtoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção se produto não existir")
    void deveLancarExcecaoSeProdutoNaoExistir() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> service.buscarPorId(999L));
    }
}