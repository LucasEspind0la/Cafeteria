package br.com.cafeteria.demo.model; // Verifique se o pacote está correto no seu projeto

import jakarta.persistence.*;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Garante que não haja dois emails iguais
    private String email;

    private String senha;

    private String role; // Ex: "ROLE_ADMIN" ou "ROLE_USER"

    // Getters e Setters são obrigatórios para o Spring acessar os dados
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public void setNome(String jwtTeste) {
    }
}