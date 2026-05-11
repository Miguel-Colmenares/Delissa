package com.delissa.service;

import com.delissa.model.User;
import com.delissa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

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
    public User login(String correo, String password) {

        User user = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // validar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return user;
    }

    public List<User> listar() {
        return userRepository.findAll();
    }

    public User actualizar(Integer id, User payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setNombre(payload.getNombre());
        user.setCedula(payload.getCedula());
        user.setCorreo(payload.getCorreo());

        if (payload.getRol() != null && !payload.getRol().isBlank()) {
            if (!payload.getRol().equals("admin") && !payload.getRol().equals("empleado")) {
                throw new RuntimeException("Rol invalido");
            }
            user.setRol(payload.getRol());
        }

        if (payload.getPassword() != null && !payload.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(payload.getPassword()));
        }

        return userRepository.save(user);
    }

    public void eliminar(Integer id) {
        userRepository.deleteById(id);
    }
}
