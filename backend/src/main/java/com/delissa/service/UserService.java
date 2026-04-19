package com.delissa.service;

import com.delissa.model.User;
import com.delissa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =========================
    // REGISTRAR USUARIO
    // =========================
    public User registrar(User user) {

        // validar rol obligatorio
        if (user.getRol() == null || user.getRol().isEmpty()) {
            throw new RuntimeException("El rol es obligatorio");
        }

        // validar roles permitidos
        if (!user.getRol().equals("admin") && !user.getRol().equals("empleado")) {
            throw new RuntimeException("Rol inválido (admin o empleado)");
        }

        // encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // =========================
    // LOGIN
    // =========================
    public User login(String correo, String password, String rol) {

        User user = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // validar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // validar rol
        if (!user.getRol().equals(rol)) {
            throw new RuntimeException("Rol incorrecto");
        }

        return user;
    }
}