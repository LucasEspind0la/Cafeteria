package br.com.cafeteria.demo.security;

import br.com.cafeteria.demo.model.Usuario;
import br.com.cafeteria.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JWT Auth - Integração")
class JwtAuthIntegrationTest {

    @Autowired private UserDetailsService userDetailsService;
    @Autowired private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setNome("JWT Teste");
        usuario.setEmail("jwt@test.com");
        usuario.setSenha("senha123");
        usuario.setRole("CLIENTE");
        usuarioRepository.save(usuario);
    }

    @Test
    @DisplayName("Deve carregar usuário por email")
    void deveCarregarUsuarioPorEmail() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("jwt@test.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("jwt@test.com");
    }

    @Test
    @DisplayName("Deve ter role CLIENTE")
    void deveTerRoleCliente() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("jwt@test.com");

        assertThat(userDetails.getAuthorities()).anyMatch(
                auth -> auth.getAuthority().equals("ROLE_CLIENTE")
        );
    }
}