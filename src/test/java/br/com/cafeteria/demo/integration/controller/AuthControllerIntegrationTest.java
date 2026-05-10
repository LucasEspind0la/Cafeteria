package br.com.cafeteria.demo.integration.controller;

import br.com.cafeteria.demo.model.Usuario;
import br.com.cafeteria.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController - Integração")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setNome("Admin Teste");
        usuario.setEmail("admin@test.com");
        usuario.setSenha(passwordEncoder.encode("123456"));
        usuario.setRole("ADMIN");
        usuarioRepository.save(usuario);
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve autenticar com sucesso")
    void deveAutenticarComSucesso() throws Exception {
        Map<String, String> login = new HashMap<>();
        login.put("email", "admin@test.com");
        login.put("senha", "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 401 com senha errada")
    void deveRetornar401ComSenhaErrada() throws Exception {
        Map<String, String> login = new HashMap<>();
        login.put("email", "admin@test.com");
        login.put("senha", "senhaerrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}