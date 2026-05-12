package br.com.cafeteria.demo;

import br.com.cafeteria.demo.model.Usuario;
import br.com.cafeteria.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 DataLoader iniciando...");
        System.out.println("   Usuários no banco: " + repository.count());

        // FORÇA recriação - remove todos primeiro (SÓ PARA DEBUG!)
        // repository.deleteAll();
        // System.out.println("🗑️ Banco limpo para debug");

        if (repository.count() == 0) {
            System.out.println("📦 Banco vazio. Criando usuários...");

            // ADMIN
            Usuario admin = new Usuario();
            admin.setEmail("admin@cafe.com");
            String senhaAdmin = encoder.encode("123456");
            admin.setSenha(senhaAdmin);
            admin.setRole("ROLE_ADMIN");
            repository.save(admin);
            System.out.println("   ✅ Admin criado: admin@cafe.com");
            System.out.println("      Hash gerado: " + senhaAdmin);

            // USER
            Usuario user = new Usuario();
            user.setEmail("user@cafe.com");
            String senhaUser = encoder.encode("123456");
            user.setSenha(senhaUser);
            user.setRole("ROLE_USER");
            repository.save(user);
            System.out.println("   ✅ User criado: user@cafe.com");
            System.out.println("      Hash gerado: " + senhaUser);

            System.out.println("✅ Usuários criados com sucesso!");
        } else {
            System.out.println("ℹ️ Usuários já existem. Pulando criação.");
            repository.findAll().forEach(u -> {
                System.out.println("   👤 " + u.getEmail() + " | " + u.getRole() + " | Senha: " + u.getSenha().substring(0, Math.min(30, u.getSenha().length())) + "...");
            });
        }
    }
}