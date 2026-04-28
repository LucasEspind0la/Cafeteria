package br.com.cafeteria.demo.integration;

import br.com.cafeteria.demo.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest              // Carrega contexto Spring completo
@AutoConfigureMockMvc        // Configura MockMvc
@Transactional             // Limpa banco após cada teste
class ProdutoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void fluxoCompleto_CRUD() throws Exception {
        // 1. CRIAR
        String json = """
            {
                "nome": "Café Teste",
                "tipo": "Bebida",
                "categoria": "Quente",
                "preco": 5.00,
                "estoque": 10,
                "descricao": "Teste"
            }
            """;

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Café Teste"));

        // 2. LISTAR
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Café Teste"));

        // 3. BUSCAR POR ID
        Long id = repository.findAll().get(0).getId();

        mockMvc.perform(get("/api/produtos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Café Teste"));

        // 4. ATUALIZAR
        String jsonUpdate = """
            {
                "nome": "Café Atualizado",
                "tipo": "Bebida",
                "categoria": "Gelado",
                "preco": 6.00,
                "estoque": 20,
                "descricao": "Atualizado"
            }
            """;

        mockMvc.perform(put("/api/produtos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Café Atualizado"))
                .andExpect(jsonPath("$.categoria").value("Gelado"));

        // 5. DELETAR
        mockMvc.perform(delete("/api/produtos/" + id))
                .andExpect(status().isNoContent());

        // 6. VERIFICAR QUE DELETOU
        mockMvc.perform(get("/api/produtos/" + id))
                .andExpect(status().isNotFound());
    }
}