package com.delissa.controller;

import com.delissa.model.User;
import com.delissa.service.UserService;
import com.delissa.dto.UserLoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // =========================
    // REGISTRO
    // =========================
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registrar(user);
    }

    // =========================
    // LOGIN (CON ROL)
    // =========================
   @PostMapping("/login")
public User login(@RequestBody UserLoginRequest request) {
    return userService.login(
        request.getCorreo(),
        request.getPassword(),
        request.getRol()
    );
}
}