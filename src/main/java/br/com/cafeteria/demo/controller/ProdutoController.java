package br.com.cafeteria.demo.controller;

import br.com.cafeteria.demo.model.Produto;
import br.com.cafeteria.demo.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite acesso de qualquer frontend
public class ProdutoController {

    private final ProdutoService produtoService;

    // GET - Listar todos
    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    // GET - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    // POST - Cadastrar
    @PostMapping
    public ResponseEntity<Produto> cadastrar(@Valid @RequestBody Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(produtoService.cadastrar(produto));
    }

    // PUT - Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id,
                                             @Valid @RequestBody Produto produto) {
        return ResponseEntity.ok(produtoService.atualizar(id, produto));
    }

    // DELETE - Remover
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // GET - Buscar por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Produto>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(produtoService.buscarPorTipo(tipo));
    }

    // GET - Estoque baixo
    @GetMapping("/estoque-baixo/{limite}")
    public ResponseEntity<List<Produto>> estoqueBaixo(@PathVariable Integer limite) {
        return ResponseEntity.ok(produtoService.estoqueBaixo(limite));
    }
}