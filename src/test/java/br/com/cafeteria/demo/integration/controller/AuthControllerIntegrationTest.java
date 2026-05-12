package br.com.cafeteria.demo.integration.controller;

import br.com.cafeteria.demo.model.Usuario;
import br.com.cafeteria.demo.repository.UsuarioRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController - Integração")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
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
    @DisplayName("POST /login - Deve autenticar e redirecionar para index")
    void deveAutenticarComSucesso() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "admin@test.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())           // 302 Redirect
                .andExpect(redirectedUrl("/index.html"));          // Redireciona para index
    }

    @Test
    @DisplayName("POST /login - Deve redirecionar para login com erro")
    void deveRetornarErroComSenhaErrada() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "admin@test.com")
                        .param("password", "senhaerrada"))
                .andExpect(status().is3xxRedirection())           // 302 Redirect
                .andExpect(redirectedUrl("/login.html?error=true")); // Redireciona com erro
    }
}