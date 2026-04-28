package br.com.cafeteria.demo.repository;

import br.com.cafeteria.demo.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar por tipo
    List<Produto> findByTipo(String tipo);

    // Buscar por categoria
    List<Produto> findByCategoria(String categoria);

    // Buscar produtos com estoque baixo
    List<Produto> findByEstoqueLessThan(Integer quantidade);
}