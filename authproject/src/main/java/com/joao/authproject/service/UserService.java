package com.joao.authproject.service;

import com.joao.authproject.model.User;
import com.joao.authproject.model.PasswordResetToken;
import com.joao.authproject.repository.UserRepository;
import com.joao.authproject.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public boolean authenticate(String email, String senha) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    System.out.println("Email buscado: " + user.getEmail());
                    System.out.println("Senha no banco: " + user.getSenha());
                    System.out.println("Senha digitada: " + senha);
                    return user.getSenha().equals(senha);
                })
                .orElseGet(() -> {
                    System.out.println("Usuário não encontrado com email: " + email);
                    return false;
                });
    }

    // --- NOVO: cria token de reset e mostra link ---
    public void createPasswordResetToken(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            tokenRepository.save(resetToken);

            System.out.println("Link para resetar senha: http://localhost:8080/reset-password?token=" + token);
        });
    }

    // --- NOVO: reseta senha via token ---
    public boolean resetPassword(String token, String newPassword) {
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(t -> {
                    User user = t.getUser();
                    user.setSenha(newPassword); // Recomendo criptografar aqui
                    userRepository.save(user);
                    tokenRepository.delete(t); // invalida o token depois do uso
                    return true;
                })
                .orElse(false);
    }
}
