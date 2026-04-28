package br.com.cafeteria.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data                    // Gera getters, setters, toString, equals, hashCode
@NoArgsConstructor       // Construtor vazio
@AllArgsConstructor      // Construtor com todos os campos
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;        // Ex: Bebida, Lanche, Doce

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;   // Ex: Quente, Gelado, Salgado, Doce

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "O estoque é obrigatório")
    @Min(value = 0, message = "O estoque não pode ser negativo")
    private Integer estoque;

    @Column(length = 500)
    private String descricao;   // Opcional
}