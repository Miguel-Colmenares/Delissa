package com.delissa.dto;

public class UserLoginRequest {

    private String correo;
    private String password;
    private String rol;

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }   // 🔥 ESTO FALTABA
    public void setRol(String rol) { this.rol = rol; }
}