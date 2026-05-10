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
import java.time.LocalDateTime;
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
                .nome("Café Expresso")
                .tipo("Bebida")
                .categoria("Cafés")
                .preco(new BigDecimal("7.90"))
                .estoque(50)
                .descricao("Café forte e encorpado")
                .imagem("http://exemplo.com/cafe.jpg")
                .build();
        produto = produtoRepository.save(produto);
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve criar pedido com sucesso")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveCriarPedidoComSucesso() throws Exception {
        // Criar item do pedido
        Map<String, Object> item = new HashMap<>();
        item.put("produtoId", produto.getId());
        item.put("quantidade", 2);

        List<Map<String, Object>> itens = new ArrayList<>();
        itens.add(item);

        // Criar request
        Map<String, Object> request = new HashMap<>();
        request.put("clienteNome", "João Teste");
        request.put("clienteTelefone", "81999999999");
        request.put("itens", itens);
        request.put("horarioRetirada", LocalDateTime.now().plusHours(1).toString());
        request.put("tipoEntrega", "RETIRADA_LOJA");
        request.put("observacoesRetirada", "Sem açúcar");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.clienteNome", is("João Teste")))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve retornar 400 sem itens")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveRetornar400SemItens() throws Exception {
        List<Map<String, Object>> itensVazios = new ArrayList<>();

        Map<String, Object> request = new HashMap<>();
        request.put("clienteNome", "João Teste");
        request.put("clienteTelefone", "81999999999");
        request.put("itens", itensVazios);
        request.put("horarioRetirada", LocalDateTime.now().plusHours(1).toString());
        request.put("tipoEntrega", "RETIRADA_LOJA");
        request.put("observacoesRetirada", null);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} - Deve buscar pedido")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveBuscarPedido() throws Exception {
        var pedido = Pedido.builder()
                .clienteNome("Maria Teste")
                .clienteTelefone("81988888888")
                .status(StatusPedido.PENDENTE)
                .total(new BigDecimal("15.80"))
                .itens(new ArrayList<>())
                .tipoEntrega(Pedido.TipoEntrega.RETIRADA_LOJA)
                .build();
        pedido = pedidoRepository.save(pedido);

        mockMvc.perform(get("/api/pedidos/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(pedido.getId().intValue())))
                .andExpect(jsonPath("$.clienteNome", is("Maria Teste")));
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve retornar 403 sem autenticação")
    void deveRetornar403SemAutenticacao() throws Exception {
        List<Map<String, Object>> itensVazios = new ArrayList<>();

        Map<String, Object> request = new HashMap<>();
        request.put("clienteNome", "João Teste");
        request.put("clienteTelefone", "81999999999");
        request.put("itens", itensVazios);
        request.put("horarioRetirada", LocalDateTime.now().plusHours(1).toString());
        request.put("tipoEntrega", "RETIRADA_LOJA");
        request.put("observacoesRetirada", null);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}