package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Este método é mágico: o Spring cria a lógica SQL automaticamente baseado no nome
    Optional<Usuario> findByEmail(String email);
}