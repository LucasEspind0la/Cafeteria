package br.com.cafeteria.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilitário para geração e validação de tokens JWT.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:cafe_manager_secret_key_2024_muito_seguro_com_pelo_menos_32_caracteres}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * Gera a chave secreta para assinatura do token.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Converte string simples em Base64 para usar como chave.
     * Use apenas se a secret NAO estiver em Base64.
     */
    private SecretKey getSigningKeyFromString() {
        String base64Secret = java.util.Base64.getEncoder()
                .encodeToString(secret.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ========== EXTRACAO DE CLAIMS ==========

    /**
     * Extrai todos os claims do token.
     */
    public Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKeyFromString())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrai um claim especifico do token.
     */
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai o username (email) do token.
     */
    public String extrairUsername(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    /**
     * Extrai a data de expiracao do token.
     */
    public Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    /**
     * Extrai as roles do token.
     */
    @SuppressWarnings("unchecked")
    public List<String> extrairRoles(String token) {
        Claims claims = extrairTodosClaims(token);
        return claims.get("roles", List.class);
    }

    // ========== GERACAO DE TOKEN ==========

    /**
     * Gera token com claims extras.
     */
    public String gerarToken(Map<String, Object> claimsExtra, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(claimsExtra);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        return criarToken(claims, userDetails.getUsername());
    }

    /**
     * Gera token simples (apenas com roles).
     */
    public String gerarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        return criarToken(claims, userDetails.getUsername());
    }

    /**
     * Cria o token JWT.
     */
    private String criarToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKeyFromString(), Jwts.SIG.HS256)
                .compact();
    }

    // ========== VALIDACAO ==========

    /**
     * Verifica se o token esta expirado.
     */
    private boolean tokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    /**
     * Valida o token completo.
     */
    public boolean validarToken(String token, UserDetails userDetails) {
        try {
            final String username = extrairUsername(token);
            return (username.equals(userDetails.getUsername()) && !tokenExpirado(token));
        } catch (ExpiredJwtException e) {
            System.err.println("Token expirado: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.err.println("Token nao suportado: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.err.println("Token malformado: " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.err.println("Assinatura invalida: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Token vazio ou nulo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida apenas se o token e valido (nao expirado e bem formado).
     */
    public boolean tokenValido(String token) {
        try {
            return !tokenExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna o tempo restante de validade em milissegundos.
     */
    public long getTempoRestante(String token) {
        Date expiracao = extrairExpiracao(token);
        return expiracao.getTime() - System.currentTimeMillis();
    }
}