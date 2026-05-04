package br.com.cafeteria.demo.controller;

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = "ADMIN")
    void testListarProdutos() throws Exception {
        // Arrange
        when(produtoService.listarTodos()).thenReturn(Arrays.asList(produto));

        // Act & Assert
        mockMvc.perform(get("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Cafe Expresso")))
                .andExpect(jsonPath("$[0].preco", is(5.00)));

        verify(produtoService, times(1)).listarTodos();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCriarProduto() throws Exception {
        // Arrange
        when(produtoService.salvar(any(Produto.class))).thenReturn(produto);

        // Act & Assert
        mockMvc.perform(post("/api/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Cafe Expresso")))
                .andExpect(jsonPath("$.tipo", is("Quente")))
                .andExpect(jsonPath("$.categoria", is("Bebida")))
                .andExpect(jsonPath("$.preco", is(5.00)))
                .andExpect(jsonPath("$.estoque", is(10)));

        verify(produtoService, times(1)).salvar(any(Produto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCriarProdutoComDadosInvalidos() throws Exception {
        // Arrange - produto sem nome
        Produto produtoInvalido = Produto.builder()
                .nome("")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .build();

        when(produtoService.salvar(any(Produto.class)))
                .thenThrow(new IllegalArgumentException("Nome do produto e obrigatorio"));

        // Act & Assert
        mockMvc.perform(post("/api/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Nome do produto e obrigatorio")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBuscarProdutoPorId() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(1L)).thenReturn(produto);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Cafe Expresso")));

        verify(produtoService, times(1)).buscarPorId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAtualizarProduto() throws Exception {
        // Arrange
        Produto produtoAtualizado = Produto.builder()
                .id(1L)
                .nome("Cafe Latte")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("7.00"))
                .estoque(20)
                .descricao("Cafe com leite")
                .imagem("https://exemplo.com/latte.jpg")
                .build();

        when(produtoService.atualizar(eq(1L), any(Produto.class))).thenReturn(produtoAtualizado);

        // Act & Assert
        mockMvc.perform(put("/api/produtos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Cafe Latte")))
                .andExpect(jsonPath("$.preco", is(7.00)));

        verify(produtoService, times(1)).atualizar(eq(1L), any(Produto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletarProduto() throws Exception {
        // Arrange
        doNothing().when(produtoService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/produtos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Produto deletado com sucesso")));

        verify(produtoService, times(1)).deletar(1L);
    }
}