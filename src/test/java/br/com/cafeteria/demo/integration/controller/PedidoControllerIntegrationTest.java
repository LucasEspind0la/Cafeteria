package br.com.cafeteria.demo.integration.controller;

import br.com.cafeteria.demo.dto.ItemRequest;
import br.com.cafeteria.demo.dto.PedidoRequest;
import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.repository.NotificacaoRepository;
import br.com.cafeteria.demo.repository.PagamentoRepository;
import br.com.cafeteria.demo.repository.PedidoRepository;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("PedidoController - Testes de Integração")
class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    private Long produtoId; // Guarda o ID real do produto criado

    @BeforeEach
    void setUp() {
        // Limpa na ordem correta (filhos primeiro, depois pais)
        notificacaoRepository.deleteAll();
        pagamentoRepository.deleteAll();
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();

        // Cria produto de teste e guarda o ID real
        Produto cafe = new Produto();
        cafe.setNome("Café");
        cafe.setDescricao("Café expresso");
        cafe.setPreco(new BigDecimal("5.00"));
        cafe.setEstoque(10);
        cafe.setCategoria("Bebidas");
        cafe.setTipo("QUENTE");
        Produto produtoSalvo = produtoRepository.save(cafe);
        this.produtoId = produtoSalvo.getId(); // Pega o ID real gerado pelo banco
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso (autenticado)")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deveCriarPedidoComSucesso() throws Exception {
        ItemRequest item = new ItemRequest();
        item.setProdutoId(produtoId); // USA O ID REAL
        item.setQuantidade(2);

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("João");
        request.setClienteTelefone("11999999999");
        request.setItens(List.of(item));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteNome").value("João"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando pedido não tem itens")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deveRetornar400SemItens() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("João");
        request.setClienteTelefone("11999999999");
        request.setItens(List.of());

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Pedido deve conter pelo menos um item"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando estoque é insuficiente")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deveRetornar400EstoqueInsuficiente() throws Exception {
        ItemRequest item = new ItemRequest();
        item.setProdutoId(produtoId); // USA O ID REAL
        item.setQuantidade(20);

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("João");
        request.setClienteTelefone("11999999999");
        request.setItens(List.of(item));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Estoque insuficiente: Café"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando não está autenticado")
    void deveRetornar403SemAutenticacao() throws Exception {
        ItemRequest item = new ItemRequest();
        item.setProdutoId(produtoId); // USA O ID REAL
        item.setQuantidade(1);

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("Anônimo");
        request.setClienteTelefone("11999999999");
        request.setItens(List.of(item));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deveListarTodosPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar pedido por ID")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deveBuscarPedidoPorId() throws Exception {
        // Primeiro cria um pedido
        ItemRequest item = new ItemRequest();
        item.setProdutoId(produtoId); // USA O ID REAL
        item.setQuantidade(1);

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("João");
        request.setClienteTelefone("11999999999");
        request.setItens(List.of(item));

        String response = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Garante que criou com sucesso
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/pedidos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteNome").value("João"));
    }

    @Test
    @DisplayName("Deve listar pedidos por status")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deveListarPedidosPorStatus() throws Exception {
        mockMvc.perform(get("/api/pedidos/status/PENDENTE"))
                .andExpect(status().isOk());
    }
}