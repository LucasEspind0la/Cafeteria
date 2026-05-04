package br.com.cafeteria.demo.integration;

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.repository.ProdutoRepository;
import br.com.cafeteria.demo.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=true"
})
class ProdutoIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFluxoCompletoCadastrarEBuscarNoBanco() throws Exception {
        // 1. Criar produto
        Produto novoProduto = Produto.builder()
                .nome("Cafe Expresso")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .descricao("Cafe forte")
                .imagem("https://exemplo.com/cafe.jpg")
                .build();

        // 2. Enviar POST para /api/produtos
        MvcResult resultadoPost = mockMvc.perform(post("/api/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Cafe Expresso"))
                .andReturn();

        // 3. Extrair ID do produto criado
        String respostaJson = resultadoPost.getResponse().getContentAsString();
        Produto produtoCriado = objectMapper.readValue(respostaJson, Produto.class);

        assertNotNull(produtoCriado.getId(), "O produto deve ter um ID gerado!");
        System.out.println("✅ Produto criado com ID: " + produtoCriado.getId());

        // 4. VERIFICAR SE ESTÁ NO BANCO (teste crítico!)
        List<Produto> produtosNoBanco = produtoRepository.findAll();
        assertFalse(produtosNoBanco.isEmpty(), "O banco deve conter pelo menos 1 produto!");
        assertTrue(produtosNoBanco.stream()
                        .anyMatch(p -> p.getNome().equals("Cafe Expresso")),
                "O produto deve estar salvo no banco!");

        System.out.println("✅ Produto encontrado no banco: " + produtosNoBanco.get(0).getNome());

        // 5. Buscar via GET /api/produtos
        mockMvc.perform(get("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Cafe Expresso"));

        System.out.println("✅ GET /api/produtos retornou o produto corretamente");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCadastrarProdutoSemNomeRetornaErro() throws Exception {
        // Produto inválido (sem nome)
        Produto produtoInvalido = Produto.builder()
                .nome("")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .build();

        mockMvc.perform(post("/api/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        // Verificar que NÃO foi salvo no banco
        assertTrue(produtoRepository.findAll().isEmpty(),
                "Produto inválido não deve ser salvo no banco!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAtualizarProduto() throws Exception {
        // 1. Criar produto primeiro
        Produto produto = Produto.builder()
                .nome("Cafe Antigo")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .build();

        Produto salvo = produtoService.salvar(produto);

        // 2. Atualizar
        Produto atualizado = Produto.builder()
                .nome("Cafe Novo")
                .tipo("Gelado")
                .categoria("Bebida")
                .preco(new BigDecimal("7.50"))
                .estoque(20)
                .descricao("Novo cafe")
                .build();

        mockMvc.perform(put("/api/produtos/" + salvo.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cafe Novo"))
                .andExpect(jsonPath("$.preco").value(7.50));

        // 3. Verificar no banco
        Produto doBanco = produtoRepository.findById(salvo.getId()).orElse(null);
        assertNotNull(doBanco);
        assertEquals("Cafe Novo", doBanco.getNome());
        assertEquals(new BigDecimal("7.50"), doBanco.getPreco());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletarProduto() throws Exception {
        // 1. Criar produto
        Produto produto = Produto.builder()
                .nome("Cafe para Deletar")
                .tipo("Quente")
                .categoria("Bebida")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .build();

        Produto salvo = produtoService.salvar(produto);
        Long id = salvo.getId();

        assertTrue(produtoRepository.existsById(id), "Produto deve existir antes de deletar");

        // 2. Deletar
        mockMvc.perform(delete("/api/produtos/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Verificar que foi removido do banco
        assertFalse(produtoRepository.existsById(id),
                "Produto deve ser removido do banco!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarProdutosVazio() throws Exception {
        // Garantir que o banco está vazio
        produtoRepository.deleteAll();

        mockMvc.perform(get("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}