package br.com.cafeteria.demo.controller;

import br.com.cafeteria.demo.model.Notificacao;
import br.com.cafeteria.demo.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    // GET /api/notificacoes/{email}
    @GetMapping("/{email}")
    public ResponseEntity<List<Notificacao>> listar(@PathVariable String email) {
        return ResponseEntity.ok(notificacaoService.listarNotificacoes(email));
    }

    // GET /api/notificacoes/{email}
    @GetMapping("/{email}/nao-lidas")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(@PathVariable String email) {
        long count = notificacaoService.contarNaoLidas(email);
        return ResponseEntity.ok(Map.of("quantidade", count));
    }

    // POST /api/notificacoes/{id}
    @PostMapping("/{id}/ler")
    public ResponseEntity<Notificacao> marcarLida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id));
    }

    // POST /api/notificacoes
    @PostMapping("/enviar")
    public ResponseEntity<Notificacao> enviar(@RequestBody Notificacao notificacao) {
        return ResponseEntity.ok(
                notificacaoService.enviarNotificacao(
                        notificacao.getClienteEmail(),
                        notificacao.getTitulo(),
                        notificacao.getMensagem(),
                        notificacao.getTipo()
                )
        );
    }
}