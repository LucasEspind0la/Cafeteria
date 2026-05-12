package br.com.cafeteria.demo.service;

import br.com.cafeteria.demo.model.Usuario;
import br.com.cafeteria.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 Buscando usuário: " + email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Garante que a role tenha o prefixo ROLE_
        String role = usuario.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        System.out.println("✅ Usuário encontrado: " + usuario.getEmail() + " | Role: " + role);

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }
}