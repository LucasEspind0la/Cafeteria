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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("JWT Auth - Integração")
class JwtAuthIntegrationTest {

    @Autowired private UserDetailsService userDetailsService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private JwtUtil jwtUtil;

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

    @Test
    @DisplayName("Deve gerar token JWT")
    void deveGerarTokenJwt() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("jwt@test.com");
        String token = jwtUtil.gerarToken(userDetails);
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Deve extrair username do token JWT")
    void deveExtrairUsernameDoToken() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("jwt@test.com");
        String token = jwtUtil.gerarToken(userDetails);
        String username = jwtUtil.extrairUsername(token);
        assertThat(username).isEqualTo("jwt@test.com");
    }

    @Test
    @DisplayName("Deve validar token JWT com sucesso")
    void deveValidarTokenJwt() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("jwt@test.com");
        String token = jwtUtil.gerarToken(userDetails);
        boolean valido = jwtUtil.validarToken(token, userDetails);
        assertThat(valido).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar token JWT inválido")
    void deveRejeitarTokenInvalido() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("jwt@test.com");
        boolean valido = jwtUtil.validarToken("token.invalido", userDetails);
        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("Deve extrair roles do token JWT")
    void deveExtrairRolesDoToken() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("jwt@test.com");
        String token = jwtUtil.gerarToken(userDetails);
        var roles = jwtUtil.extrairRoles(token);
        assertThat(roles).contains("ROLE_CLIENTE");
    }
}