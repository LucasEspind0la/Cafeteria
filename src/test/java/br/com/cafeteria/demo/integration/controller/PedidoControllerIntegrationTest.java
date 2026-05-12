package br.com.cafeteria.demo.integration.controller;

import br.com.cafeteria.demo.model.*;
import br.com.cafeteria.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("PedidoController - Integração")
class PedidoControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private PedidoRepository pedidoRepository;

    private Produto produto;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();

        produto = Produto.builder()
                .nome("Cafe")
                .tipo("QUENTE")
                .categoria("Bebidas")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .descricao("Cafe expresso")
                .build();
        produto = produtoRepository.save(produto);
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve criar pedido com sucesso (201)")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveCriarPedidoComSucesso() throws Exception {
        List<Map<String, Object>> itens = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("produtoId", produto.getId());
        item.put("quantidade", 2);
        itens.add(item);

        Map<String, Object> request = new HashMap<>();
        request.put("clienteNome", "Joao");
        request.put("clienteTelefone", "11999999999");
        request.put("itens", itens);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.clienteNome", is("Joao")))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve retornar 400 sem itens")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveRetornar400SemItens() throws Exception {
        List<Map<String, Object>> itensVazios = new ArrayList<>();

        Map<String, Object> request = new HashMap<>();
        request.put("clienteNome", "Joao");
        request.put("clienteTelefone", "11999999999");
        request.put("itens", itensVazios);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} - Deve buscar pedido por id (200)")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveBuscarPedidoPorId() throws Exception {
        Pedido pedido = Pedido.builder()
                .clienteNome("Maria")
                .clienteTelefone("11888888888")
                .status(StatusPedido.PENDENTE)
                .total(new BigDecimal("15.00"))
                .itens(new ArrayList<>())
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();
        pedido = pedidoRepository.save(pedido);

        mockMvc.perform(get("/api/pedidos/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(pedido.getId().intValue())))
                .andExpect(jsonPath("$.clienteNome", is("Maria")));
    }

    @Test
    @DisplayName("GET /api/pedidos/status/PENDENTE - Deve listar pedidos por status")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveListarPedidosPorStatus() throws Exception {
        mockMvc.perform(get("/api/pedidos/status/PENDENTE"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pedidos - Deve listar todos os pedidos")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveListarTodosPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve redirecionar para login sem autenticacao (302)")
    void deveRedirecionarParaLoginSemAutenticacao() throws Exception {
        List<Map<String, Object>> itens = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("produtoId", produto.getId());
        item.put("quantidade", 1);
        itens.add(item);

        Map<String, Object> request = new HashMap<>();
        request.put("clienteNome", "Anonimo");
        request.put("clienteTelefone", "11999999999");
        request.put("itens", itens);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login.html"));  // ✅ CORRIGIDO: URL exata
    }
}