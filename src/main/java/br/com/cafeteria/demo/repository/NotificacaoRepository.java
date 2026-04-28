package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByClienteEmailOrderByDataEnvioDesc(String clienteEmail);
    List<Notificacao> findByClienteEmailAndLidaFalse(String clienteEmail);
    long countByClienteEmailAndLidaFalse(String clienteEmail);
}