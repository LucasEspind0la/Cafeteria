package br.com.cafeteria.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clienteNome;

    @Column(nullable = false)
    private String clienteTelefone;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private BigDecimal total;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<ItemPedido> itens = new ArrayList<>();

    private LocalDateTime dataCriacao;

    @Column(name = "horario_retirada")
    private LocalDateTime horarioRetirada;

    @Column(name = "tipo_entrega")
    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega = TipoEntrega.RETIRADA_LOJA;

    @Column(name = "observacoes_retirada")
    private String observacoesRetirada;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
    }

    public enum TipoEntrega {
        RETIRADA_LOJA, DELIVERY
    }
}