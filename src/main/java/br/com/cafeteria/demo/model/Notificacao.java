package br.com.cafeteria.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteEmail;
    private String titulo;
    private String mensagem;
    private Boolean lida = false;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    private LocalDateTime dataEnvio = LocalDateTime.now();

    public enum TipoNotificacao {
        PEDIDO_RECEBIDO, PEDIDO_PRONTO, PEDIDO_ENTREGUE,
        PROMOCAO, RECOMPENSA_FIDELIDADE
    }

    public Notificacao() {}

    public Notificacao(String clienteEmail, String titulo, String mensagem, TipoNotificacao tipo) {
        this.clienteEmail = clienteEmail;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }

    public TipoNotificacao getTipo() { return tipo; }
    public void setTipo(TipoNotificacao tipo) { this.tipo = tipo; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
}