package br.com.cafeteria.demo.controller; //

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // <--- CORREÇÃO 2: Use MockBean
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // <--- CORREÇÃO 2: Substitui @MockitoBean
    private ProdutoService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornarListaDeProdutos() throws Exception {
        // Given
        Produto p = new Produto();
        p.setId(1L);
        p.setNome("Café");
        p.setPreco(new BigDecimal("5.00"));

        when(service.listarTodos()).thenReturn(Arrays.asList(p));

        // When + Then
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Café"))
                .andExpect(jsonPath("$[0].preco").value(5.00));
    }

    @Test
    void deveCadastrarProduto() throws Exception {
        Produto p = new Produto();
        p.setNome("Cappuccino");
        p.setTipo("Bebida");
        p.setCategoria("Quente");
        p.setPreco(new BigDecimal("8.50"));
        p.setEstoque(30);

        when(service.cadastrar(any())).thenReturn(p);

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated()) // Certifique-se que seu controller retorna 201 Created
                .andExpect(jsonPath("$.nome").value("Cappuccino"));
    }

    @Test
    void deveDeletarProduto() throws Exception {
        doNothing().when(service).deletar(1L);

        mockMvc.perform(delete("/api/produtos/1"))
                .andExpect(status().isNoContent()); // Certifique-se que seu controller retorna 204 No Content
    }
}